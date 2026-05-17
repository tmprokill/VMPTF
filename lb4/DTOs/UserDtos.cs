namespace lb4.DTOs;

public record UserResponse(int Id, string Username, string Email, string Role, DateTime CreatedAt);
public record UpdateUserRequest(string? Username, string? Email);
