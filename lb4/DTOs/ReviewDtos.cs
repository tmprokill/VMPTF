namespace lb4.DTOs;

public record ReviewRequest(int ProductId, int Rating, string? Comment);
public record ReviewResponse(int Id, int UserId, string Username, int ProductId, string ProductName, int Rating, string? Comment, DateTime CreatedAt);
