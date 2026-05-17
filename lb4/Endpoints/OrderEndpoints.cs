using System.Security.Claims;
using lb4.Data;
using lb4.DTOs;
using lb4.Models;
using Microsoft.EntityFrameworkCore;

namespace lb4.Endpoints;

public static class OrderEndpoints
{
    public static void MapOrderEndpoints(this WebApplication app)
    {
        var group = app.MapGroup("/orders").WithTags("Orders").RequireAuthorization();

        group.MapGet("/", async (AppDbContext db, ClaimsPrincipal caller) =>
        {
            var callerId = int.Parse(caller.FindFirst(ClaimTypes.NameIdentifier)!.Value);
            var isAdmin = caller.IsInRole("Admin");
            
            var query = db.Orders
                .Include(o => o.Items)
                    .ThenInclude(i => i.Product)
                .AsQueryable();

            if (!isAdmin)
                query = query.Where(o => o.UserId == callerId);

            var orders = await query
                .Select(o => new OrderResponse(
                    o.Id, o.UserId, o.Status, o.CreatedAt, o.TotalAmount,
                    o.Items.Select(i => new OrderItemResponse(i.ProductId, i.Product.Name, i.Quantity, i.UnitPrice)).ToList()))
                .ToListAsync();

            return Results.Ok(orders);
        })
        .WithSummary("List orders (own orders or all for Admin)");

        group.MapGet("/{id:int}", async (int id, AppDbContext db, ClaimsPrincipal caller) =>
        {
            var callerId = int.Parse(caller.FindFirst(ClaimTypes.NameIdentifier)!.Value);

            var order = await db.Orders
                .Include(o => o.Items).ThenInclude(i => i.Product)
                .FirstOrDefaultAsync(o => o.Id == id);

            if (order is null) return Results.NotFound();
            if (order.UserId != callerId && !caller.IsInRole("Admin")) return Results.Forbid();

            return Results.Ok(new OrderResponse(
                order.Id, order.UserId, order.Status, order.CreatedAt, order.TotalAmount,
                order.Items.Select(i => new OrderItemResponse(i.ProductId, i.Product.Name, i.Quantity, i.UnitPrice)).ToList()));
        })
        .WithSummary("Get an order by id");

        group.MapPost("/", async (CreateOrderRequest req, AppDbContext db, ClaimsPrincipal caller) =>
        {
            var callerId = int.Parse(caller.FindFirst(ClaimTypes.NameIdentifier)!.Value);

            if (req.Items.Count == 0)
                return Results.BadRequest(new { error = "Order must have at least one item" });

            var productIds = req.Items.Select(i => i.ProductId).Distinct().ToList();
            var products = await db.Products.Where(p => productIds.Contains(p.Id)).ToListAsync();

            if (products.Count != productIds.Count)
                return Results.BadRequest(new { error = "One or more products not found" });

            var order = new Order
            {
                UserId = callerId,
                Items = req.Items.Select(i =>
                {
                    var price = products.First(p => p.Id == i.ProductId).Price;
                    return new OrderItem { ProductId = i.ProductId, Quantity = i.Quantity, UnitPrice = price };
                }).ToList()
            };
            order.TotalAmount = order.Items.Sum(i => i.Quantity * i.UnitPrice);

            db.Orders.Add(order);
            await db.SaveChangesAsync();

            await db.Entry(order).Collection(o => o.Items).Query().Include(i => i.Product).LoadAsync();

            return Results.Created($"/orders/{order.Id}", new OrderResponse(
                order.Id, order.UserId, order.Status, order.CreatedAt, order.TotalAmount,
                order.Items.Select(i => new OrderItemResponse(i.ProductId, i.Product.Name, i.Quantity, i.UnitPrice)).ToList()));
        })
        .WithSummary("Create an order");

        group.MapPut("/{id:int}/status", async (int id, UpdateOrderStatusRequest req, AppDbContext db) =>
        {
            var order = await db.Orders.FindAsync(id);
            if (order is null) return Results.NotFound();

            order.Status = req.Status;
            await db.SaveChangesAsync();
            return Results.Ok(new { order.Id, order.Status });
        })
        .RequireAuthorization(p => p.RequireRole("Admin"))
        .WithSummary("Update order status (Admin)");

        group.MapDelete("/{id:int}", async (int id, AppDbContext db, ClaimsPrincipal caller) =>
        {
            var callerId = int.Parse(caller.FindFirst(ClaimTypes.NameIdentifier)!.Value);

            var order = await db.Orders.FindAsync(id);
            if (order is null) return Results.NotFound();
            if (order.UserId != callerId && !caller.IsInRole("Admin")) return Results.Forbid();

            db.Orders.Remove(order);
            await db.SaveChangesAsync();
            return Results.NoContent();
        })
        .WithSummary("Cancel/delete an order");
    }
}
