using lb4.Data;
using lb4.DTOs;
using lb4.Models;
using Microsoft.EntityFrameworkCore;

namespace lb4.Endpoints;

public static class ProductEndpoints
{
    public static void MapProductEndpoints(this WebApplication app)
    {
        var group = app.MapGroup("/products").WithTags("Products");

        group.MapGet("/", async (AppDbContext db) =>
        {
            var list = await db.Products
                .Include(p => p.Category)
                .Select(p => new ProductResponse(p.Id, p.Name, p.Description, p.Price, p.Stock, p.CategoryId, p.Category.Name))
                .ToListAsync();
            return Results.Ok(list);
        })
        .WithSummary("List all products");

        group.MapGet("/{id:int}", async (int id, AppDbContext db) =>
        {
            var product = await db.Products
                .Include(p => p.Category)
                .Where(p => p.Id == id)
                .Select(p => new ProductResponse(p.Id, p.Name, p.Description, p.Price, p.Stock, p.CategoryId, p.Category.Name))
                .FirstOrDefaultAsync();
            return product is null ? Results.NotFound() : Results.Ok(product);
        })
        .WithSummary("Get a product by id");

        group.MapGet("/category/{categoryId:int}", async (int categoryId, AppDbContext db) =>
        {
            var list = await db.Products
                .Where(p => p.CategoryId == categoryId)
                .Include(p => p.Category)
                .Select(p => new ProductResponse(p.Id, p.Name, p.Description, p.Price, p.Stock, p.CategoryId, p.Category.Name))
                .ToListAsync();
            return Results.Ok(list);
        })
        .WithSummary("List products by category");

        group.MapPost("/", async (ProductRequest req, AppDbContext db) =>
        {
            if (!await db.Categories.AnyAsync(c => c.Id == req.CategoryId))
                return Results.BadRequest(new { error = "Category not found" });

            var product = new Product
            {
                Name = req.Name,
                Description = req.Description,
                Price = req.Price,
                Stock = req.Stock,
                CategoryId = req.CategoryId
            };
            db.Products.Add(product);
            await db.SaveChangesAsync();

            await db.Entry(product).Reference(p => p.Category).LoadAsync();
            return Results.Created($"/products/{product.Id}",
                new ProductResponse(product.Id, product.Name, product.Description, product.Price, product.Stock, product.CategoryId, product.Category.Name));
        })
        .RequireAuthorization(p => p.RequireRole("Admin"))
        .WithSummary("Create a product (Admin)");

        group.MapPut("/{id:int}", async (int id, ProductRequest req, AppDbContext db) =>
        {
            var product = await db.Products.FindAsync(id);
            if (product is null) return Results.NotFound();

            if (!await db.Categories.AnyAsync(c => c.Id == req.CategoryId))
                return Results.BadRequest(new { error = "Category not found" });

            product.Name = req.Name;
            product.Description = req.Description;
            product.Price = req.Price;
            product.Stock = req.Stock;
            product.CategoryId = req.CategoryId;
            await db.SaveChangesAsync();

            await db.Entry(product).Reference(p => p.Category).LoadAsync();
            return Results.Ok(new ProductResponse(product.Id, product.Name, product.Description, product.Price, product.Stock, product.CategoryId, product.Category.Name));
        })
        .RequireAuthorization(p => p.RequireRole("Admin"))
        .WithSummary("Update a product (Admin)");

        group.MapDelete("/{id:int}", async (int id, AppDbContext db) =>
        {
            var product = await db.Products.FindAsync(id);
            if (product is null) return Results.NotFound();

            db.Products.Remove(product);
            await db.SaveChangesAsync();
            return Results.NoContent();
        })
        .RequireAuthorization(p => p.RequireRole("Admin"))
        .WithSummary("Delete a product (Admin)");
    }
}
