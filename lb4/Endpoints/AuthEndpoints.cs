using lb4.Data;
using lb4.DTOs;
using lb4.Models;
using lb4.Services;
using Microsoft.EntityFrameworkCore;

namespace lb4.Endpoints;

public static class AuthEndpoints
{
    public static void MapAuthEndpoints(this WebApplication app)
    {
        var group = app.MapGroup("/auth").WithTags("Auth");

        group.MapPost("/register", async (RegisterRequest req, AppDbContext db, TokenService tokens) =>
        {
            if (await db.Users.AnyAsync(u => u.Email == req.Email))
                return Results.Conflict(new { error = "Email already registered" });

            if (await db.Users.AnyAsync(u => u.Username == req.Username))
                return Results.Conflict(new { error = "Username already taken" });

            var user = new User
            {
                Username = req.Username,
                Email = req.Email,
                PasswordHash = BCrypt.Net.BCrypt.HashPassword(req.Password)
            };

            db.Users.Add(user);
            await db.SaveChangesAsync();

            return Results.Ok(new AuthResponse(tokens.GenerateToken(user), user.Username, user.Role));
        })
        .WithSummary("Register a new user");

        group.MapPost("/login", async (LoginRequest req, AppDbContext db, TokenService tokens) =>
        {
            var user = await db.Users.FirstOrDefaultAsync(u => u.Email == req.Email);

            if (user is null || !BCrypt.Net.BCrypt.Verify(req.Password, user.PasswordHash))
                return Results.Unauthorized();

            return Results.Ok(new AuthResponse(tokens.GenerateToken(user), user.Username, user.Role));
        })
        .WithSummary("Login and receive a JWT token");
    }
}
