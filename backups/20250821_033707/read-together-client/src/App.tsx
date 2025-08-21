
import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { ThemeProvider } from "@/contexts/ThemeContext";
import Index from "./pages/Index";
import Profile from "./pages/Profile";
import EditProfile from "./pages/EditProfile";
import Chat from "./pages/Chat";
import ReadingRooms from "./pages/ReadingRooms";
import ReadingRoom from "./pages/ReadingRoom";
import PublicProfile from "./pages/PublicProfile";
import AccountSettings from "./pages/AccountSettings";
import Privacy from "./pages/Privacy";
import Terms from "./pages/Terms";
import Contact from "./pages/Contact";
import Guidelines from "./pages/Guidelines";
import Accessibility from "./pages/Accessibility";
import Cookies from "./pages/Cookies";
import HelpCenter from "./pages/HelpCenter";
import Feedback from "./pages/Feedback";
import NotFound from "./pages/NotFound";

const queryClient = new QueryClient();

const App = () => (
  <QueryClientProvider client={queryClient}>
    <ThemeProvider>
      <TooltipProvider>
        <Toaster />
        <Sonner />
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<Index />} />
            <Route path="/profile" element={<Profile />} />
            <Route path="/profile/edit" element={<EditProfile />} />
            <Route path="/chat" element={<Chat />} />
            <Route path="/rooms" element={<ReadingRooms />} />
            <Route path="/room/:roomId" element={<ReadingRoom />} />
            <Route path="/profile/:username" element={<PublicProfile />} />
            <Route path="/settings" element={<AccountSettings />} />
            <Route path="/privacy" element={<Privacy />} />
            <Route path="/terms" element={<Terms />} />
            <Route path="/contact" element={<Contact />} />
            <Route path="/guidelines" element={<Guidelines />} />
            <Route path="/accessibility" element={<Accessibility />} />
            <Route path="/cookies" element={<Cookies />} />
            <Route path="/help" element={<HelpCenter />} />
            <Route path="/feedback" element={<Feedback />} />
            {/* ADD ALL CUSTOM ROUTES ABOVE THE CATCH-ALL "*" ROUTE */}
            <Route path="*" element={<NotFound />} />
          </Routes>
        </BrowserRouter>
      </TooltipProvider>
    </ThemeProvider>
  </QueryClientProvider>
);

export default App;
