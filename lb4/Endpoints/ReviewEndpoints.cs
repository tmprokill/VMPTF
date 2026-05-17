using System.Security.Claims;
using lb4.Data;
using lb4.DTOs;
using lb4.Models;
using Microsoft.EntityFrameworkCore;

namespace lb4.Endpoints;

public static class ReviewEndpoints
{
    public static void MapReviewEndpoints(this WebApplication app)
    {
        var group = app.MapGroup("/reviews").WithTags("Reviews");

        group.MapGet("/product/{productId:int}", async (int productId, AppDbContext db) =>
        {
            var reviews = await db.Reviews
                .Where(r => r.ProductId == productId)
                .Include(r => r.User)
                .Include(r => r.Product)
                .Select(r => new ReviewResponse(r.Id, r.UserId, r.User.Username, r.ProductId, r.Product.Name, r.Rating, r.Comment, r.CreatedAt))
                .ToListAsync();
            return Results.Ok(reviews);
        })
        .WithSummary("List reviews for a product");

        group.MapPost("/", async (ReviewRequest req, AppDbContext db, ClaimsPrincipal caller) =>
        {
            var callerId = int.Parse(caller.FindFirst(ClaimTypes.NameIdentifier)!.Value);

            if (!await db.Products.AnyAsync(p => p.Id == req.ProductId))
                return Results.BadRequest(new { error = "Product not found" });

            if (req.Rating is < 1 or > 5)
                return Results.BadRequest(new { error = "Rating must be between 1 and 5" });

            if (await db.Reviews.AnyAsync(r => r.UserId == callerId && r.ProductId == req.ProductId))
                return Results.Conflict(new { error = "You already reviewed this product" });

            var review = new Review
            {
                UserId = callerId,
                ProductId = req.ProductId,
                Rating = req.Rating,
                Comment = req.Comment
            };
            db.Reviews.Add(review);
            await db.SaveChangesAsync();

            await db.Entry(review).Reference(r => r.User).LoadAsync();
            await db.Entry(review).Reference(r => r.Product).LoadAsync();

            return Results.Created($"/reviews/{review.Id}",
                new ReviewResponse(review.Id, review.UserId, review.User.Username, review.ProductId, review.Product.Name, review.Rating, review.Comment, review.CreatedAt));
        })
        .RequireAuthorization()
        .WithSummary("Create a review");

        group.MapPut("/{id:int}", async (int id, ReviewRequest req, AppDbContext db, ClaimsPrincipal caller) =>
        {
            var callerId = int.Parse(caller.FindFirst(ClaimTypes.NameIdentifier)!.Value);

            var review = await db.Reviews.FindAsync(id);
            if (review is null) return Results.NotFound();
            if (review.UserId != callerId && !caller.IsInRole("Admin")) return Results.Forbid();

            if (req.Rating is < 1 or > 5)
                return Results.BadRequest(new { error = "Rating must be between 1 and 5" });

            review.Rating = req.Rating;
            review.Comment = req.Comment;
            await db.SaveChangesAsync();

            await db.Entry(review).Reference(r => r.User).LoadAsync();
            await db.Entry(review).Reference(r => r.Product).LoadAsync();

            return Results.Ok(new ReviewResponse(review.Id, review.UserId, review.User.Username, review.ProductId, review.Product.Name, review.Rating, review.Comment, review.CreatedAt));
        })
        .RequireAuthorization()
        .WithSummary("Update a review (own or Admin)");

        group.MapDelete("/{id:int}", async (int id, AppDbContext db, ClaimsPrincipal caller) =>
        {
            var callerId = int.Parse(caller.FindFirst(ClaimTypes.NameIdentifier)!.Value);

            var review = await db.Reviews.FindAsync(id);
            if (review is null) return Results.NotFound();
            if (review.UserId != callerId && !caller.IsInRole("Admin")) return Results.Forbid();

            db.Reviews.Remove(review);
            await db.SaveChangesAsync();
            return Results.NoContent();
        })
        .RequireAuthorization()
        .WithSummary("Delete a review (own or Admin)");
    }
}
