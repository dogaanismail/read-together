import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Switch } from "@/components/ui/switch";
import { Separator } from "@/components/ui/separator";
import { ArrowLeft, Camera, Save, X } from "lucide-react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { toast } from "sonner";
import Navigation from "@/components/Navigation";

const profileSchema = z.object({
  firstName: z.string().min(2, "First name must be at least 2 characters"),
  lastName: z.string().min(2, "Last name must be at least 2 characters"),
  username: z.string().min(3, "Username must be at least 3 characters"),
  email: z.string().email("Invalid email address"),
  bio: z.string().max(500, "Bio must be less than 500 characters"),
  location: z.string().optional(),
  website: z.string().url("Invalid URL").optional().or(z.literal("")),
  nativeLanguage: z.string().min(1, "Please select your native language"),
  learningLanguages: z.array(z.string()).min(1, "Please select at least one learning language"),
  profileVisibility: z.enum(["public", "private"]),
  showProgress: z.boolean(),
  allowMessages: z.boolean(),
});

type ProfileFormValues = z.infer<typeof profileSchema>;

const EditProfile = () => {
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);

  // Mock current user data
  const currentUser = {
    firstName: "Sarah",
    lastName: "Johnson", 
    username: "sarah_learns",
    email: "sarah@example.com",
    bio: "Passionate language learner exploring French and Spanish cultures through conversation.",
    location: "New York, USA",
    website: "https://sarahlearns.blog",
    avatar: "https://images.unsplash.com/photo-1494790108755-2616b612b47c?w=150&h=150&fit=crop&crop=face",
    nativeLanguage: "English",
    learningLanguages: ["French", "Spanish"],
    profileVisibility: "public" as const,
    showProgress: true,
    allowMessages: true,
  };

  const form = useForm<ProfileFormValues>({
    resolver: zodResolver(profileSchema),
    defaultValues: currentUser,
  });

  const onSubmit = async (values: ProfileFormValues) => {
    setIsLoading(true);
    try {
      // Mock API call
      await new Promise(resolve => setTimeout(resolve, 1000));
      console.log("Profile updated:", values);
      toast.success("Profile updated successfully!");
      navigate("/profile");
    } catch (error) {
      toast.error("Failed to update profile. Please try again.");
    } finally {
      setIsLoading(false);
    }
  };

  const languages = ["English", "Spanish", "French", "German", "Italian", "Portuguese", "Mandarin", "Japanese", "Korean", "Arabic"];

  return (
    <>
      <Navigation />
      <div className="min-h-screen bg-gradient-to-br from-background via-background/95 to-primary/5 dark:from-background dark:via-background/98 dark:to-primary/10">
        <div className="container mx-auto px-4 py-8">
          <div className="max-w-4xl mx-auto">
            {/* Header */}
            <div className="flex items-center gap-4 mb-8">
              <Button
                variant="ghost"
                size="sm"
                onClick={() => navigate("/profile")}
                className="text-muted-foreground hover:text-foreground dark:text-muted-foreground dark:hover:text-foreground"
              >
                <ArrowLeft className="h-4 w-4 mr-2" />
                Back to Profile
              </Button>
            </div>

            <Form {...form}>
              <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                {/* Profile Picture & Basic Info */}
                <Card className="backdrop-blur-sm bg-card/80 border-border/50 dark:bg-card/30 dark:backdrop-blur-md dark:border-border/30">
                  <CardHeader>
                    <CardTitle className="text-foreground dark:text-foreground">Profile Information</CardTitle>
                    <CardDescription className="text-muted-foreground dark:text-muted-foreground">
                      Update your profile picture and basic information
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-6">
                    {/* Avatar Section */}
                    <div className="flex items-center gap-6">
                      <div className="relative">
                        <Avatar className="h-24 w-24">
                          <AvatarImage src={currentUser.avatar} alt="Profile picture" />
                          <AvatarFallback className="text-lg bg-muted dark:bg-muted/50">
                            {currentUser.firstName[0]}{currentUser.lastName[0]}
                          </AvatarFallback>
                        </Avatar>
                        <Button
                          type="button"
                          size="sm"
                          variant="secondary"
                          className="absolute -bottom-2 -right-2 h-8 w-8 rounded-full p-0 bg-secondary/80 hover:bg-secondary dark:bg-secondary/50 dark:hover:bg-secondary/70"
                        >
                          <Camera className="h-4 w-4" />
                        </Button>
                      </div>
                      <div className="flex-1 space-y-2">
                        <p className="text-sm text-muted-foreground dark:text-muted-foreground">
                          Upload a new profile picture. Max file size: 5MB.
                        </p>
                        <div className="flex gap-2">
                          <Button type="button" variant="outline" size="sm">
                            Upload Image
                          </Button>
                          <Button type="button" variant="ghost" size="sm" className="text-destructive">
                            Remove
                          </Button>
                        </div>
                      </div>
                    </div>

                    <Separator className="dark:bg-border/30" />

                    {/* Name Fields */}
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <FormField
                        control={form.control}
                        name="firstName"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel className="text-foreground dark:text-foreground">First Name</FormLabel>
                            <FormControl>
                              <Input 
                                placeholder="Enter your first name" 
                                {...field}
                                className="bg-background/50 border-border/50 text-foreground dark:bg-background/20 dark:border-border/30 dark:text-foreground"
                              />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                      <FormField
                        control={form.control}
                        name="lastName"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel className="text-foreground dark:text-foreground">Last Name</FormLabel>
                            <FormControl>
                              <Input 
                                placeholder="Enter your last name" 
                                {...field}
                                className="bg-background/50 border-border/50 text-foreground dark:bg-background/20 dark:border-border/30 dark:text-foreground"
                              />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                    </div>

                    {/* Username & Email */}
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <FormField
                        control={form.control}
                        name="username"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel className="text-foreground dark:text-foreground">Username</FormLabel>
                            <FormControl>
                              <Input 
                                placeholder="Enter your username" 
                                {...field}
                                className="bg-background/50 border-border/50 text-foreground dark:bg-background/20 dark:border-border/30 dark:text-foreground"
                              />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                      <FormField
                        control={form.control}
                        name="email"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel className="text-foreground dark:text-foreground">Email</FormLabel>
                            <FormControl>
                              <Input 
                                placeholder="Enter your email" 
                                type="email"
                                {...field}
                                className="bg-background/50 border-border/50 text-foreground dark:bg-background/20 dark:border-border/30 dark:text-foreground"
                              />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                    </div>
                  </CardContent>
                </Card>

                {/* Bio & Location */}
                <Card className="backdrop-blur-sm bg-card/80 border-border/50 dark:bg-card/30 dark:backdrop-blur-md dark:border-border/30">
                  <CardHeader>
                    <CardTitle className="text-foreground dark:text-foreground">About You</CardTitle>
                    <CardDescription className="text-muted-foreground dark:text-muted-foreground">
                      Tell others about yourself and your language learning journey
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    <FormField
                      control={form.control}
                      name="bio"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel className="text-foreground dark:text-foreground">Bio</FormLabel>
                          <FormControl>
                            <Textarea 
                              placeholder="Write a short bio about yourself and your language learning goals..."
                              className="min-h-[100px] bg-background/50 border-border/50 text-foreground dark:bg-background/20 dark:border-border/30 dark:text-foreground"
                              {...field}
                            />
                          </FormControl>
                          <div className="text-xs text-muted-foreground dark:text-muted-foreground text-right">
                            {field.value?.length || 0}/500 characters
                          </div>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <FormField
                        control={form.control}
                        name="location"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel className="text-foreground dark:text-foreground">Location</FormLabel>
                            <FormControl>
                              <Input 
                                placeholder="e.g., New York, USA" 
                                {...field}
                                className="bg-background/50 border-border/50 text-foreground dark:bg-background/20 dark:border-border/30 dark:text-foreground"
                              />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                      <FormField
                        control={form.control}
                        name="website"
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel className="text-foreground dark:text-foreground">Website</FormLabel>
                            <FormControl>
                              <Input 
                                placeholder="https://yourwebsite.com" 
                                {...field}
                                className="bg-background/50 border-border/50 text-foreground dark:bg-background/20 dark:border-border/30 dark:text-foreground"
                              />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />
                    </div>
                  </CardContent>
                </Card>

                {/* Language Settings */}
                <Card className="backdrop-blur-sm bg-card/80 border-border/50 dark:bg-card/30 dark:backdrop-blur-md dark:border-border/30">
                  <CardHeader>
                    <CardTitle className="text-foreground dark:text-foreground">Language Preferences</CardTitle>
                    <CardDescription className="text-muted-foreground dark:text-muted-foreground">
                      Set your native language and languages you're learning
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    <FormField
                      control={form.control}
                      name="nativeLanguage"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel className="text-foreground dark:text-foreground">Native Language</FormLabel>
                          <Select onValueChange={field.onChange} defaultValue={field.value}>
                            <FormControl>
                              <SelectTrigger className="bg-background/50 border-border/50 text-foreground dark:bg-background/20 dark:border-border/30 dark:text-foreground">
                                <SelectValue placeholder="Select your native language" />
                              </SelectTrigger>
                            </FormControl>
                            <SelectContent>
                              {languages.map((language) => (
                                <SelectItem key={language} value={language}>
                                  {language}
                                </SelectItem>
                              ))}
                            </SelectContent>
                          </Select>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                  </CardContent>
                </Card>

                {/* Privacy Settings */}
                <Card className="backdrop-blur-sm bg-card/80 border-border/50 dark:bg-card/30 dark:backdrop-blur-md dark:border-border/30">
                  <CardHeader>
                    <CardTitle className="text-foreground dark:text-foreground">Privacy & Visibility</CardTitle>
                    <CardDescription className="text-muted-foreground dark:text-muted-foreground">
                      Control who can see your profile and interact with you
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-6">
                    <FormField
                      control={form.control}
                      name="profileVisibility"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel className="text-foreground dark:text-foreground">Profile Visibility</FormLabel>
                          <Select onValueChange={field.onChange} defaultValue={field.value}>
                            <FormControl>
                              <SelectTrigger className="bg-background/50 border-border/50 text-foreground dark:bg-background/20 dark:border-border/30 dark:text-foreground">
                                <SelectValue />
                              </SelectTrigger>
                            </FormControl>
                            <SelectContent>
                              <SelectItem value="public">Public - Anyone can view your profile</SelectItem>
                              <SelectItem value="private">Private - Only you can view your profile</SelectItem>
                            </SelectContent>
                          </Select>
                          <FormMessage />
                        </FormItem>
                      )}
                    />

                    <div className="space-y-4">
                      <FormField
                        control={form.control}
                        name="showProgress"
                        render={({ field }) => (
                          <FormItem className="flex flex-row items-center justify-between rounded-lg border border-border/50 p-4 dark:border-border/30">
                            <div className="space-y-0.5">
                              <FormLabel className="text-base text-foreground dark:text-foreground">
                                Show Learning Progress
                              </FormLabel>
                              <div className="text-sm text-muted-foreground dark:text-muted-foreground">
                                Allow others to see your learning statistics and progress
                              </div>
                            </div>
                            <FormControl>
                              <Switch
                                checked={field.value}
                                onCheckedChange={field.onChange}
                              />
                            </FormControl>
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="allowMessages"
                        render={({ field }) => (
                          <FormItem className="flex flex-row items-center justify-between rounded-lg border border-border/50 p-4 dark:border-border/30">
                            <div className="space-y-0.5">
                              <FormLabel className="text-base text-foreground dark:text-foreground">
                                Allow Messages
                              </FormLabel>
                              <div className="text-sm text-muted-foreground dark:text-muted-foreground">
                                Let other users send you direct messages
                              </div>
                            </div>
                            <FormControl>
                              <Switch
                                checked={field.value}
                                onCheckedChange={field.onChange}
                              />
                            </FormControl>
                          </FormItem>
                        )}
                      />
                    </div>
                  </CardContent>
                </Card>

                {/* Action Buttons */}
                <div className="flex justify-end gap-4">
                  <Button
                    type="button"
                    variant="outline"
                    onClick={() => navigate("/profile")}
                    disabled={isLoading}
                  >
                    <X className="h-4 w-4 mr-2" />
                    Cancel
                  </Button>
                  <Button
                    type="submit"
                    disabled={isLoading}
                    className="bg-primary hover:bg-primary/90 text-primary-foreground"
                  >
                    <Save className="h-4 w-4 mr-2" />
                    {isLoading ? "Saving..." : "Save Changes"}
                  </Button>
                </div>
              </form>
            </Form>
          </div>
        </div>
      </div>
    </>
  );
};

export default EditProfile;