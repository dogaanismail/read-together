import { useState, useRef, useEffect } from "react";
import { Send, Phone, Video, MoreVertical, Search, Paperclip, Smile } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Separator } from "@/components/ui/separator";
import Navigation from "@/components/Navigation";
import EmojiPicker from "@/components/EmojiPicker";
import FileAttachment from "@/components/FileAttachment";

interface User {
  id: string;
  name: string;
  username: string;
  avatar?: string;
  online: boolean;
  lastSeen?: string;
}

interface Message {
  id: string;
  senderId: string;
  content: string;
  timestamp: Date;
  type: 'text' | 'image' | 'file' | 'emoji';
  attachments?: AttachedFile[];
}

interface AttachedFile {
  id: string;
  name: string;
  size: number;
  type: string;
  url?: string;
}

interface ChatRoom {
  id: string;
  name: string;
  participants: User[];
  lastMessage?: Message;
  unreadCount: number;
  type: 'direct' | 'group';
}

const Chat = () => {
  const [selectedChat, setSelectedChat] = useState<string | null>(null);
  const [newMessage, setNewMessage] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [showEmojiPicker, setShowEmojiPicker] = useState(false);
  const [attachedFiles, setAttachedFiles] = useState<AttachedFile[]>([]);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  // Mock current user
  const currentUser: User = {
    id: "current-user",
    name: "John Doe",
    username: "johndoe",
    avatar: "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face",
    online: true
  };

  // Mock users
  const users: User[] = [
    {
      id: "user1",
      name: "Sarah Johnson",
      username: "sarah_learns",
      avatar: "https://images.unsplash.com/photo-1494790108755-2616b612b47c?w=150&h=150&fit=crop&crop=face",
      online: true
    },
    {
      id: "user2", 
      name: "Michael Chen",
      username: "mike_reads",
      avatar: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face",
      online: false,
      lastSeen: "2 hours ago"
    },
    {
      id: "user3",
      name: "Emma Wilson",
      username: "emma_stories",
      avatar: "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150&h=150&fit=crop&crop=face", 
      online: true
    },
    {
      id: "user4",
      name: "David Brown",
      username: "david_lang",
      online: false,
      lastSeen: "1 day ago"
    }
  ];

  // Mock chat rooms
  const chatRooms: ChatRoom[] = [
    {
      id: "chat1",
      name: "Sarah Johnson",
      participants: [currentUser, users[0]],
      lastMessage: {
        id: "msg1",
        senderId: "user1", 
        content: "Hey! How's your French practice going?",
        timestamp: new Date(Date.now() - 1000 * 60 * 5),
        type: 'text'
      },
      unreadCount: 2,
      type: 'direct'
    },
    {
      id: "chat2", 
      name: "Michael Chen",
      participants: [currentUser, users[1]],
      lastMessage: {
        id: "msg2",
        senderId: "current-user",
        content: "Thanks for the book recommendation!",
        timestamp: new Date(Date.now() - 1000 * 60 * 30),
        type: 'text'
      },
      unreadCount: 0,
      type: 'direct'
    },
    {
      id: "chat3",
      name: "Language Exchange Group",
      participants: [currentUser, ...users.slice(0, 3)],
      lastMessage: {
        id: "msg3",
        senderId: "user3",
        content: "Anyone up for a Spanish conversation session?",
        timestamp: new Date(Date.now() - 1000 * 60 * 60 * 2),
        type: 'text'
      },
      unreadCount: 5,
      type: 'group'
    }
  ];

  // Mock messages for selected chat
  const messages: Message[] = [
    {
      id: "1",
      senderId: "user1",
      content: "Hey John! I saw your latest reading session. Your pronunciation is getting so much better!",
      timestamp: new Date(Date.now() - 1000 * 60 * 60),
      type: 'text'
    },
    {
      id: "2", 
      senderId: "current-user",
      content: "Thank you! I've been practicing every day. Your feedback really helps.",
      timestamp: new Date(Date.now() - 1000 * 60 * 55),
      type: 'text'
    },
    {
      id: "3",
      senderId: "user1", 
      content: "Would you like to do a conversation exchange? I'm working on my English accent and you could practice French with me.",
      timestamp: new Date(Date.now() - 1000 * 60 * 50),
      type: 'text'
    },
    {
      id: "4",
      senderId: "current-user",
      content: "That sounds perfect! When would be a good time for you?",
      timestamp: new Date(Date.now() - 1000 * 60 * 45),
      type: 'text'
    },
    {
      id: "5",
      senderId: "user1",
      content: "How about this weekend? We could start with 30 minutes each language.",
      timestamp: new Date(Date.now() - 1000 * 60 * 5),
      type: 'text'
    }
  ];

  // Get selected chat details
  const selectedChatData = selectedChat ? chatRooms.find(chat => chat.id === selectedChat) : null;
  const chatPartner = selectedChatData?.type === 'direct' 
    ? selectedChatData.participants.find(p => p.id !== currentUser.id)
    : null;

  // Filter chats based on search
  const filteredChats = chatRooms.filter(chat =>
    chat.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleSendMessage = () => {
    if ((!newMessage.trim() && attachedFiles.length === 0) || !selectedChat) return;

    // Here you would send the message to your Spring Boot backend
    const messageData = {
      chatId: selectedChat,
      content: newMessage,
      senderId: currentUser.id,
      attachments: attachedFiles,
      type: attachedFiles.length > 0 ? 'file' : 'text'
    };
    
    console.log("Sending message:", messageData);

    // Reset form
    setNewMessage("");
    setAttachedFiles([]);
    setShowEmojiPicker(false);
  };

  const handleEmojiSelect = (emoji: string) => {
    setNewMessage(prev => prev + emoji);
  };

  const handleRemoveFile = (fileId: string) => {
    setAttachedFiles(prev => prev.filter(f => f.id !== fileId));
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const formatTime = (date: Date) => {
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };

  return (
    <>
      <Navigation />
      <div className="h-screen bg-gradient-to-br from-background via-background/95 to-primary/5 dark:from-background dark:via-background/98 dark:to-primary/10">
        <div className="flex h-full pt-16">
          {/* Chat Sidebar */}
          <div className="w-80 border-r border-border/50 bg-card/80 backdrop-blur-sm dark:bg-card/20 dark:border-border/30">
            <div className="p-4 border-b border-border/50 dark:border-border/30">
              <h1 className="text-xl font-semibold text-foreground dark:text-foreground mb-3">Messages</h1>
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                <Input
                  placeholder="Search conversations..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="pl-10 bg-background/50 border-border/50 text-foreground dark:bg-background/20 dark:border-border/30 dark:text-foreground"
                />
              </div>
            </div>

            <ScrollArea className="h-full pb-32">
              <div className="p-2">
                {filteredChats.map((chat) => {
                  const partner = chat.type === 'direct' 
                    ? chat.participants.find(p => p.id !== currentUser.id)
                    : null;
                  
                  return (
                    <div
                      key={chat.id}
                      onClick={() => setSelectedChat(chat.id)}
                      className={`p-3 rounded-lg cursor-pointer transition-colors mb-1 ${
                        selectedChat === chat.id
                          ? 'bg-primary/10 dark:bg-primary/20'
                          : 'hover:bg-muted/50 dark:hover:bg-muted/20'
                      }`}
                    >
                      <div className="flex items-center gap-3">
                        <div className="relative">
                          <Avatar className="h-12 w-12">
                            <AvatarImage src={partner?.avatar} />
                            <AvatarFallback className="bg-muted text-muted-foreground dark:bg-muted/50">
                              {chat.type === 'group' ? 'GR' : chat.name[0]}
                            </AvatarFallback>
                          </Avatar>
                          {chat.type === 'direct' && partner?.online && (
                            <div className="absolute -bottom-0.5 -right-0.5 h-3 w-3 bg-green-500 rounded-full border-2 border-background" />
                          )}
                        </div>
                        
                        <div className="flex-1 min-w-0">
                          <div className="flex items-center justify-between">
                            <h3 className="font-medium text-foreground dark:text-foreground truncate">
                              {chat.name}
                            </h3>
                            {chat.lastMessage && (
                              <span className="text-xs text-muted-foreground dark:text-muted-foreground">
                                {formatTime(chat.lastMessage.timestamp)}
                              </span>
                            )}
                          </div>
                          
                          <div className="flex items-center justify-between mt-1">
                            <p className="text-sm text-muted-foreground dark:text-muted-foreground truncate">
                              {chat.lastMessage?.content || 'No messages yet'}
                            </p>
                            {chat.unreadCount > 0 && (
                              <Badge variant="default" className="ml-2 h-5 min-w-5 text-xs bg-primary">
                                {chat.unreadCount}
                              </Badge>
                            )}
                          </div>
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
            </ScrollArea>
          </div>

          {/* Chat Area */}
          <div className="flex-1 flex flex-col">
            {selectedChatData ? (
              <>
                {/* Chat Header */}
                <div className="p-4 border-b border-border/50 bg-card/80 backdrop-blur-sm dark:bg-card/20 dark:border-border/30">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <Avatar className="h-10 w-10">
                        <AvatarImage src={chatPartner?.avatar} />
                        <AvatarFallback className="bg-muted text-muted-foreground dark:bg-muted/50">
                          {selectedChatData.type === 'group' ? 'GR' : selectedChatData.name[0]}
                        </AvatarFallback>
                      </Avatar>
                      <div>
                        <h2 className="font-semibold text-foreground dark:text-foreground">
                          {selectedChatData.name}
                        </h2>
                        <p className="text-sm text-muted-foreground dark:text-muted-foreground">
                          {selectedChatData.type === 'direct' 
                            ? (chatPartner?.online ? 'Online' : `Last seen ${chatPartner?.lastSeen}`)
                            : `${selectedChatData.participants.length} members`
                          }
                        </p>
                      </div>
                    </div>
                    
                    <div className="flex items-center gap-2">
                      <Button variant="ghost" size="sm">
                        <Phone className="h-4 w-4" />
                      </Button>
                      <Button variant="ghost" size="sm">
                        <Video className="h-4 w-4" />
                      </Button>
                      <Button variant="ghost" size="sm">
                        <MoreVertical className="h-4 w-4" />
                      </Button>
                    </div>
                  </div>
                </div>

                {/* Messages Area */}
                <ScrollArea className="flex-1 p-4">
                  <div className="space-y-4">
                    {messages.map((message) => {
                      const isOwnMessage = message.senderId === currentUser.id;
                      const sender = isOwnMessage 
                        ? currentUser 
                        : users.find(u => u.id === message.senderId);
                      
                      return (
                        <div
                          key={message.id}
                          className={`flex gap-3 ${isOwnMessage ? 'flex-row-reverse' : 'flex-row'}`}
                        >
                          {!isOwnMessage && (
                            <Avatar className="h-8 w-8 flex-shrink-0">
                              <AvatarImage src={sender?.avatar} />
                              <AvatarFallback className="text-xs bg-muted dark:bg-muted/50">
                                {sender?.name[0]}
                              </AvatarFallback>
                            </Avatar>
                          )}
                          
                          <div className={`max-w-[70%] ${isOwnMessage ? 'items-end' : 'items-start'} flex flex-col`}>
                            <div
                              className={`rounded-2xl px-4 py-2 ${
                                isOwnMessage
                                  ? 'bg-primary text-primary-foreground'
                                  : 'bg-muted text-foreground dark:bg-muted/50 dark:text-foreground'
                              }`}
                            >
                              <p className="text-sm">{message.content}</p>
                            </div>
                            <span className="text-xs text-muted-foreground dark:text-muted-foreground mt-1">
                              {formatTime(message.timestamp)}
                            </span>
                          </div>
                        </div>
                      );
                    })}
                    <div ref={messagesEndRef} />
                  </div>
                </ScrollArea>

                 {/* Message Input */}
                 <div className="p-4 border-t border-border/50 bg-card/80 backdrop-blur-sm dark:bg-card/20 dark:border-border/30">
                   <div className="relative">
                     {/* File Attachment Component - Show previews above input */}
                     <FileAttachment
                       attachedFiles={attachedFiles}
                       onFilesChange={setAttachedFiles}
                       onRemoveFile={handleRemoveFile}
                     />

                     {/* Hidden file input for attachment button */}
                     <input
                       ref={fileInputRef}
                       type="file"
                       multiple
                       onChange={(e) => {
                         const files = e.target.files;
                         if (!files) return;
                          const newFiles: AttachedFile[] = Array.from(files).map(file => ({
                            id: Math.random().toString(36).substring(2, 11),
                            name: file.name,
                            size: file.size,
                            type: file.type,
                            url: URL.createObjectURL(file)
                          }));
                         setAttachedFiles(prev => [...prev, ...newFiles]);
                       }}
                       className="hidden"
                       accept="image/*,video/*,audio/*,.pdf,.doc,.docx,.txt,.zip,.rar"
                     />

                     {/* Emoji Picker */}
                     {showEmojiPicker && (
                       <div className="relative">
                         <EmojiPicker
                           onEmojiSelect={handleEmojiSelect}
                           onClose={() => setShowEmojiPicker(false)}
                         />
                       </div>
                     )}

                      <div className="flex items-end gap-2">
                        <Button 
                          type="button"
                          variant="ghost" 
                          size="sm" 
                          onClick={() => fileInputRef.current?.click()}
                          className="flex-shrink-0"
                        >
                          <Paperclip className="h-4 w-4" />
                        </Button>
                       
                       <div className="flex-1 relative">
                         <Input
                           placeholder="Type a message..."
                           value={newMessage}
                           onChange={(e) => setNewMessage(e.target.value)}
                           onKeyPress={handleKeyPress}
                           className="pr-10 bg-background/50 border-border/50 text-foreground dark:bg-background/20 dark:border-border/30 dark:text-foreground"
                         />
                         <Button 
                           type="button"
                           variant="ghost" 
                           size="sm" 
                           onClick={() => setShowEmojiPicker(!showEmojiPicker)}
                           className="absolute right-1 top-1/2 transform -translate-y-1/2 h-8 w-8 p-0"
                         >
                           <Smile className="h-4 w-4" />
                         </Button>
                       </div>
                       
                       <Button 
                         onClick={handleSendMessage}
                         disabled={!newMessage.trim() && attachedFiles.length === 0}
                         className="flex-shrink-0"
                       >
                         <Send className="h-4 w-4" />
                       </Button>
                     </div>

                     {/* File preview indicator */}
                     {attachedFiles.length > 0 && (
                       <div className="mt-2 text-xs text-muted-foreground dark:text-muted-foreground">
                         {attachedFiles.length} file{attachedFiles.length > 1 ? 's' : ''} attached
                       </div>
                     )}
                   </div>
                 </div>
              </>
            ) : (
              /* No Chat Selected */
              <div className="flex-1 flex items-center justify-center bg-muted/20 dark:bg-muted/10">
                <div className="text-center">
                  <div className="h-20 w-20 mx-auto mb-4 rounded-full bg-muted dark:bg-muted/50 flex items-center justify-center">
                    <Send className="h-8 w-8 text-muted-foreground dark:text-muted-foreground" />
                  </div>
                  <h3 className="text-lg font-medium text-foreground dark:text-foreground mb-2">
                    Select a conversation
                  </h3>
                  <p className="text-muted-foreground dark:text-muted-foreground">
                    Choose from your existing conversations or start a new one
                  </p>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </>
  );
};

export default Chat;