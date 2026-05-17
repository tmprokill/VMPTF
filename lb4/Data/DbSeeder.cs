using lb4.Models;
using Microsoft.EntityFrameworkCore;

namespace lb4.Data;

public static class DbSeeder
{
    public static async Task SeedAsync(AppDbContext db)
    {
        if (await db.Users.AnyAsync()) return; // already seeded

        // ── Users ──
        var admin = new User
        {
            Username = "admin",
            Email = "admin@shop.com",
            PasswordHash = BCrypt.Net.BCrypt.HashPassword("Admin123!"),
            Role = "Admin"
        };
        var alice = new User
        {
            Username = "alice",
            Email = "alice@shop.com",
            PasswordHash = BCrypt.Net.BCrypt.HashPassword("Alice123!")
        };
        var bob = new User
        {
            Username = "bob",
            Email = "bob@shop.com",
            PasswordHash = BCrypt.Net.BCrypt.HashPassword("Bob123!")
        };
        db.Users.AddRange(admin, alice, bob);

        // ── Categories ──
        var electronics = new Category { Name = "Electronics", Description = "Gadgets and devices" };
        var clothing    = new Category { Name = "Clothing",    Description = "Apparel and accessories" };
        var books       = new Category { Name = "Books",       Description = "Printed and digital books" };
        var sports      = new Category { Name = "Sports",      Description = "Equipment and activewear" };
        db.Categories.AddRange(electronics, clothing, books, sports);

        // ── Products ───
        var laptop = new Product
        {
            Name = "Laptop Pro 15", Description = "High-performance laptop with 16 GB RAM",
            Price = 1299.99m, Stock = 25, Category = electronics
        };
        var headphones = new Product
        {
            Name = "Wireless Headphones", Description = "Noise-cancelling over-ear headphones",
            Price = 199.99m, Stock = 60, Category = electronics
        };
        var smartphone = new Product
        {
            Name = "Smartphone X12", Description = "Flagship phone with 256 GB storage",
            Price = 899.99m, Stock = 40, Category = electronics
        };
        var tShirt = new Product
        {
            Name = "Cotton T-Shirt", Description = "Comfortable everyday t-shirt",
            Price = 19.99m, Stock = 200, Category = clothing
        };
        var jacket = new Product
        {
            Name = "Winter Jacket", Description = "Waterproof insulated jacket",
            Price = 129.99m, Stock = 50, Category = clothing
        };
        var cleanCode = new Product
        {
            Name = "Clean Code", Description = "A handbook of agile software craftsmanship by Robert C. Martin",
            Price = 34.99m, Stock = 80, Category = books
        };
        var designPatterns = new Product
        {
            Name = "Design Patterns", Description = "Elements of reusable object-oriented software",
            Price = 44.99m, Stock = 55, Category = books
        };
        var yogaMat = new Product
        {
            Name = "Yoga Mat", Description = "Non-slip exercise mat, 6mm thick",
            Price = 29.99m, Stock = 120, Category = sports
        };
        db.Products.AddRange(laptop, headphones, smartphone, tShirt, jacket, cleanCode, designPatterns, yogaMat);

        // ── Orders ──
        var order1 = new Order
        {
            User = alice,
            Status = "Delivered",
            CreatedAt = DateTime.UtcNow.AddDays(-10),
            Items =
            [
                new OrderItem { Product = laptop,     Quantity = 1, UnitPrice = laptop.Price },
                new OrderItem { Product = headphones, Quantity = 1, UnitPrice = headphones.Price }
            ]
        };
        order1.TotalAmount = order1.Items.Sum(i => i.Quantity * i.UnitPrice);

        var order2 = new Order
        {
            User = bob,
            Status = "Processing",
            CreatedAt = DateTime.UtcNow.AddDays(-2),
            Items =
            [
                new OrderItem { Product = cleanCode,     Quantity = 2, UnitPrice = cleanCode.Price },
                new OrderItem { Product = designPatterns, Quantity = 1, UnitPrice = designPatterns.Price }
            ]
        };
        order2.TotalAmount = order2.Items.Sum(i => i.Quantity * i.UnitPrice);

        var order3 = new Order
        {
            User = alice,
            Status = "Pending",
            CreatedAt = DateTime.UtcNow,
            Items =
            [
                new OrderItem { Product = yogaMat, Quantity = 1, UnitPrice = yogaMat.Price },
                new OrderItem { Product = tShirt,  Quantity = 3, UnitPrice = tShirt.Price }
            ]
        };
        order3.TotalAmount = order3.Items.Sum(i => i.Quantity * i.UnitPrice);
        db.Orders.AddRange(order1, order2, order3);

        // ── Reviews ──
        db.Reviews.AddRange(
            new Review { User = alice, Product = laptop,      Rating = 5, Comment = "Blazing fast, great display!",       CreatedAt = DateTime.UtcNow.AddDays(-8) },
            new Review { User = alice, Product = headphones,  Rating = 4, Comment = "Excellent sound, slightly tight fit.", CreatedAt = DateTime.UtcNow.AddDays(-7) },
            new Review { User = bob,   Product = cleanCode,   Rating = 5, Comment = "Must-read for every developer.",       CreatedAt = DateTime.UtcNow.AddDays(-1) },
            new Review { User = bob,   Product = designPatterns, Rating = 5, Comment = "Classic, timeless reference.",      CreatedAt = DateTime.UtcNow.AddDays(-1) },
            new Review { User = alice, Product = yogaMat,     Rating = 4, Comment = "Good grip, a bit thin.",               CreatedAt = DateTime.UtcNow }
        );

        await db.SaveChangesAsync();
    }
}
