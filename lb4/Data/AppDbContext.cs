using lb4.Models;
using Microsoft.EntityFrameworkCore;

namespace lb4.Data;

public class AppDbContext(DbContextOptions<AppDbContext> options) : DbContext(options)
{
    public DbSet<User> Users => Set<User>();
    public DbSet<Category> Categories => Set<Category>();
    public DbSet<Product> Products => Set<Product>();
    public DbSet<Order> Orders => Set<Order>();
    public DbSet<OrderItem> OrderItems => Set<OrderItem>();
    public DbSet<Review> Reviews => Set<Review>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        // Unique constraints
        modelBuilder.Entity<User>().HasIndex(u => u.Email).IsUnique();
        modelBuilder.Entity<User>().HasIndex(u => u.Username).IsUnique();

        // Query-optimisation indexes
        modelBuilder.Entity<Product>().HasIndex(p => p.CategoryId);
        modelBuilder.Entity<Order>().HasIndex(o => o.UserId);
        modelBuilder.Entity<OrderItem>().HasIndex(oi => oi.OrderId);
        modelBuilder.Entity<OrderItem>().HasIndex(oi => oi.ProductId);
        modelBuilder.Entity<Review>().HasIndex(r => r.ProductId);
        modelBuilder.Entity<Review>().HasIndex(r => r.UserId);
        // Composite index speeds up "did this user review this product?" checks
        modelBuilder.Entity<Review>().HasIndex(r => new { r.UserId, r.ProductId }).IsUnique();

        // Relationships
        // Category 1 -> N Products
        modelBuilder.Entity<Product>()
            .HasOne(p => p.Category)
            .WithMany(c => c.Products)
            .HasForeignKey(p => p.CategoryId)
            .OnDelete(DeleteBehavior.Restrict);

        // User 1 -> N Orders
        modelBuilder.Entity<Order>()
            .HasOne(o => o.User)
            .WithMany(u => u.Orders)
            .HasForeignKey(o => o.UserId)
            .OnDelete(DeleteBehavior.Restrict);

        // Order 1 -> N OrderItems (cascade: removing an order removes its lines)
        modelBuilder.Entity<OrderItem>()
            .HasOne(oi => oi.Order)
            .WithMany(o => o.Items)
            .HasForeignKey(oi => oi.OrderId)
            .OnDelete(DeleteBehavior.Cascade);

        // Product 1 -> N OrderItems
        modelBuilder.Entity<OrderItem>()
            .HasOne(oi => oi.Product)
            .WithMany(p => p.OrderItems)
            .HasForeignKey(oi => oi.ProductId)
            .OnDelete(DeleteBehavior.Restrict);

        // User 1 -> N Reviews
        modelBuilder.Entity<Review>()
            .HasOne(r => r.User)
            .WithMany(u => u.Reviews)
            .HasForeignKey(r => r.UserId)
            .OnDelete(DeleteBehavior.Restrict);

        // Product 1 -> N Reviews (cascade: removing a product removes its reviews)
        modelBuilder.Entity<Review>()
            .HasOne(r => r.Product)
            .WithMany(p => p.Reviews)
            .HasForeignKey(r => r.ProductId)
            .OnDelete(DeleteBehavior.Cascade);
    }
}
