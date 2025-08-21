// Feature flags for development
export const FEATURE_FLAGS = {
  // Set to true to bypass authentication for UI testing
  BYPASS_AUTH: true,

  // Mock user data for UI testing when auth is bypassed
  MOCK_USER_DATA: {
    id: "mock-user-id",
    email: "test@example.com",
    firstName: "John",
    lastName: "Doe",
    username: "johndoe",
    profilePictureUrl: "",
    bio: "I love reading and learning new things!",
    readingStreak: 15,
    totalSessions: 42,
    totalReadingTimeSeconds: 18000, // 5 hours
    longestStreak: 30,
    totalActiveDays: 85,
  }
};

