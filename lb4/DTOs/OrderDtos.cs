namespace lb4.DTOs;

public record OrderItemRequest(int ProductId, int Quantity);
public record CreateOrderRequest(List<OrderItemRequest> Items);
public record UpdateOrderStatusRequest(string Status);
public record OrderItemResponse(int ProductId, string ProductName, int Quantity, decimal UnitPrice);
public record OrderResponse(int Id, int UserId, string Status, DateTime CreatedAt, decimal TotalAmount, List<OrderItemResponse> Items);
