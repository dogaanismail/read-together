# Read Together Client

This is the frontend for the "Read Together" application, a platform designed to bring people together to read books in a shared virtual environment.

## Features

Based on the project structure, here are some of the core features:

- **User Authentication**: Secure login and registration system (`LoginPage.tsx`, `RegisterPage.tsx`).
- **Reading Rooms**: Users can create, join, and manage virtual reading rooms (`CreateRoomModal.tsx`, `JoinRoomModal.tsx`, `ReadingRoom.tsx`).
- **Real-time Interaction**: In-room chat functionality (`Chat.tsx`) and audio/video capabilities (`AudioVideoPlayer.tsx`).
- **Personal Library**: A personal space for users to manage their books (`BookLibrary.tsx`).
- **User Profiles**: Customizable user profiles with public and private views (`Profile.tsx`, `EditProfile.tsx`, `PublicProfile.tsx`).
- **Activity Tracking**: Features to monitor reading progress, such as activity graphs, streaks, and goals (`ReadingActivityGraph.tsx`, `ReadingStreaks.tsx`, `ReadingGoals.tsx`).
- **Social Features**: Invite others to reading rooms and view session cards (`InvitePeopleModal.tsx`, `SessionCard.tsx`).
- **Notifications**: A notification system to keep users updated (`NotificationDropdown.tsx`, `NotificationBadge.tsx`).

## Project Structure

The frontend codebase is organized into the following main directories:

- **`src/pages`**: Contains the top-level components for each page of the application, such as `DashboardPage.tsx`, `ReadingRoom.tsx`, and `Profile.tsx`.

- **`src/components`**: Holds reusable components that are used across different pages.
  - **`src/components/ui`**: A collection of generic, reusable UI elements like buttons, cards, and dialogs, likely from a component library like Shadcn/UI.
  - Other components in this directory are more feature-specific, such as `BookLibrary.tsx` or `ReadingActivityGraph.tsx`.

- **`src/contexts`**: Provides React Context for managing global state, such as `AuthContext.tsx` for authentication and `ThemeContext.tsx` for theme management.

- **`src/hooks`**: Contains custom React hooks that encapsulate and reuse stateful logic, like `useNotifications.tsx` and `useSpeechRecognition.tsx`.

- **`src/lib`**: A library of helper functions, utilities, and API communication logic.
  - **`src/lib/api`**: Functions for making requests to the backend API.
  - `utils.ts`: General utility functions.

## Getting Started

To get the project up and running on your local machine, follow these steps:

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd read-together-client
    ```

2.  **Install dependencies:**
    This project uses `bun` as the package manager.
    ```bash
    bun install
    ```

3.  **Run the development server:**
    ```bash
    bun dev
    ```
    This will start the application on a local development server, typically at `http://localhost:5173`.

## Technologies Used

- **Framework**: [React](https://reactjs.org/)
- **Build Tool**: [Vite](https://vitejs.dev/)
- **Language**: [TypeScript](https://www.typescriptlang.org/)
- **Styling**: [Tailwind CSS](https://tailwindcss.com/)
- **UI Components**: Shadcn/UI (inferred from `components.json` and `src/components/ui`)
- **Package Manager**: [Bun](https://bun.sh/)

