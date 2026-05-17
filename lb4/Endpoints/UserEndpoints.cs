using System.Security.Claims;
using lb4.Data;
using lb4.DTOs;
using Microsoft.EntityFrameworkCore;

namespace lb4.Endpoints;

public static class UserEndpoints
{
    public static void MapUserEndpoints(this WebApplication app)
    {
        var group = app.MapGroup("/users").WithTags("Users").RequireAuthorization();

        group.MapGet("/", async (AppDbContext db) =>
        {
            var users = await db.Users
                .Select(u => new UserResponse(u.Id, u.Username, u.Email, u.Role, u.CreatedAt))
                .ToListAsync();
            return Results.Ok(users);
        })
        .RequireAuthorization(p => p.RequireRole("Admin"))
        .WithSummary("List all users (Admin)");

        group.MapGet("/{id:int}", async (int id, AppDbContext db, ClaimsPrincipal caller) =>
        {
            var callerId = int.Parse(caller.FindFirst(ClaimTypes.NameIdentifier)!.Value);
            if (callerId != id && !caller.IsInRole("Admin"))
                return Results.Forbid();

            var user = await db.Users
                .Where(u => u.Id == id)
                .Select(u => new UserResponse(u.Id, u.Username, u.Email, u.Role, u.CreatedAt))
                .FirstOrDefaultAsync();

            return user is null ? Results.NotFound() : Results.Ok(user);
        })
        .WithSummary("Get a user by id (own profile or Admin)");

        group.MapPut("/{id:int}", async (int id, UpdateUserRequest req, AppDbContext db, ClaimsPrincipal caller) =>
        {
            var callerId = int.Parse(caller.FindFirst(ClaimTypes.NameIdentifier)!.Value);
            if (callerId != id && !caller.IsInRole("Admin"))
                return Results.Forbid();

            var user = await db.Users.FindAsync(id);
            if (user is null) return Results.NotFound();

            if (req.Username is not null) user.Username = req.Username;
            if (req.Email is not null) user.Email = req.Email;

            await db.SaveChangesAsync();
            return Results.Ok(new UserResponse(user.Id, user.Username, user.Email, user.Role, user.CreatedAt));
        })
        .WithSummary("Update a user (own profile or Admin)");

        group.MapDelete("/{id:int}", async (int id, AppDbContext db) =>
        {
            var user = await db.Users.FindAsync(id);
            if (user is null) return Results.NotFound();

            db.Users.Remove(user);
            await db.SaveChangesAsync();
            return Results.NoContent();
        })
        .RequireAuthorization(p => p.RequireRole("Admin"))
        .WithSummary("Delete a user (Admin)");
    }
}
