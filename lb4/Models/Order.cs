namespace lb4.Models;

public class Order
{
    public int Id { get; set; }
    public int UserId { get; set; }
    public string Status { get; set; } = "Pending";
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public decimal TotalAmount { get; set; }

    public User User { get; set; } = null!;
    public ICollection<OrderItem> Items { get; set; } = [];
}
