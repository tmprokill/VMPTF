using lb4.Data;
using lb4.DTOs;
using lb4.Models;
using Microsoft.EntityFrameworkCore;

namespace lb4.Endpoints;

public static class CategoryEndpoints
{
    public static void MapCategoryEndpoints(this WebApplication app)
    {
        var group = app.MapGroup("/categories").WithTags("Categories");

        group.MapGet("/", async (AppDbContext db) =>
        {
            var list = await db.Categories
                .Select(c => new CategoryResponse(c.Id, c.Name, c.Description))
                .ToListAsync();
            return Results.Ok(list);
        })
        .WithSummary("List all categories");

        group.MapGet("/{id:int}", async (int id, AppDbContext db) =>
        {
            var cat = await db.Categories
                .Where(c => c.Id == id)
                .Select(c => new CategoryResponse(c.Id, c.Name, c.Description))
                .FirstOrDefaultAsync();
            return cat is null ? Results.NotFound() : Results.Ok(cat);
        })
        .WithSummary("Get a category by id");

        group.MapPost("/", async (CategoryRequest req, AppDbContext db) =>
        {
            var cat = new Category { Name = req.Name, Description = req.Description };
            db.Categories.Add(cat);
            await db.SaveChangesAsync();
            return Results.Created($"/categories/{cat.Id}", new CategoryResponse(cat.Id, cat.Name, cat.Description));
        })
        .RequireAuthorization(p => p.RequireRole("Admin"))
        .WithSummary("Create a category (Admin)");

        group.MapPut("/{id:int}", async (int id, CategoryRequest req, AppDbContext db) =>
        {
            var cat = await db.Categories.FindAsync(id);
            if (cat is null) return Results.NotFound();

            cat.Name = req.Name;
            cat.Description = req.Description;
            await db.SaveChangesAsync();
            return Results.Ok(new CategoryResponse(cat.Id, cat.Name, cat.Description));
        })
        .RequireAuthorization(p => p.RequireRole("Admin"))
        .WithSummary("Update a category (Admin)");

        group.MapDelete("/{id:int}", async (int id, AppDbContext db) =>
        {
            var cat = await db.Categories.FindAsync(id);
            if (cat is null) return Results.NotFound();

            db.Categories.Remove(cat);
            await db.SaveChangesAsync();
            return Results.NoContent();
        })
        .RequireAuthorization(p => p.RequireRole("Admin"))
        .WithSummary("Delete a category (Admin)");
    }
}
