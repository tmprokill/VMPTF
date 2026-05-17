namespace lb4.DTOs;

public record ProductRequest(string Name, string? Description, decimal Price, int Stock, int CategoryId);
public record ProductResponse(int Id, string Name, string? Description, decimal Price, int Stock, int CategoryId, string CategoryName);
