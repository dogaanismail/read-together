import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { 
  Copy, Mail, Share2, Users, Link, QrCode, MessageCircle, 
  Check, X, UserPlus, Send, Search
} from "lucide-react";
import { toast } from "sonner";
import { Separator } from "@/components/ui/separator";
import { Textarea } from "@/components/ui/textarea";

interface InvitePeopleModalProps {
  isOpen: boolean;
  onClose: () => void;
  roomId: string;
  roomTitle: string;
  isPrivate: boolean;
}

interface Contact {
  id: string;
  name: string;
  email: string;
  avatar?: string;
  isOnline: boolean;
}

const InvitePeopleModal = ({ isOpen, onClose, roomId, roomTitle, isPrivate }: InvitePeopleModalProps) => {
  const [activeTab, setActiveTab] = useState<'link' | 'email' | 'contacts'>('link');
  const [emails, setEmails] = useState('');
  const [message, setMessage] = useState(`Join me for a reading session: "${roomTitle}"`);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedContacts, setSelectedContacts] = useState<string[]>([]);
  const [isSending, setIsSending] = useState(false);

  // Mock contacts data
  const contacts: Contact[] = [
    {
      id: '1',
      name: 'Alice Johnson',
      email: 'alice@example.com',
      avatar: 'https://images.unsplash.com/photo-1494790108755-2616b612b47c?w=150&h=150&fit=crop&crop=face',
      isOnline: true
    },
    {
      id: '2',
      name: 'Bob Smith',
      email: 'bob@example.com',
      avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face',
      isOnline: false
    },
    {
      id: '3',
      name: 'Carol Davis',
      email: 'carol@example.com',
      avatar: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150&h=150&fit=crop&crop=face',
      isOnline: true
    },
    {
      id: '4',
      name: 'David Wilson',
      email: 'david@example.com',
      isOnline: false
    }
  ];

  const roomUrl = `${window.location.origin}/room/${roomId}`;

  const filteredContacts = contacts.filter(contact =>
    contact.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    contact.email.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleCopyLink = async () => {
    try {
      await navigator.clipboard.writeText(roomUrl);
      toast.success('Room link copied to clipboard!');
    } catch (error) {
      toast.error('Failed to copy link');
    }
  };

  const handleSendEmails = async () => {
    if (!emails.trim()) {
      toast.error('Please enter at least one email address');
      return;
    }

    setIsSending(true);
    try {
      // Here you would send emails via your backend
      const emailList = emails.split(',').map(email => email.trim()).filter(Boolean);
      console.log('Sending invites to:', emailList);
      console.log('Message:', message);
      
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 2000));
      
      toast.success(`Invitations sent to ${emailList.length} people!`);
      setEmails('');
      setMessage(`Join me for a reading session: "${roomTitle}"`);
    } catch (error) {
      toast.error('Failed to send invitations');
    } finally {
      setIsSending(false);
    }
  };

  const handleSendToContacts = async () => {
    if (selectedContacts.length === 0) {
      toast.error('Please select at least one contact');
      return;
    }

    setIsSending(true);
    try {
      const selectedContactsData = contacts.filter(c => selectedContacts.includes(c.id));
      console.log('Sending invites to contacts:', selectedContactsData);
      
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1500));
      
      toast.success(`Invitations sent to ${selectedContacts.length} contacts!`);
      setSelectedContacts([]);
    } catch (error) {
      toast.error('Failed to send invitations');
    } finally {
      setIsSending(false);
    }
  };

  const toggleContact = (contactId: string) => {
    setSelectedContacts(prev =>
      prev.includes(contactId)
        ? prev.filter(id => id !== contactId)
        : [...prev, contactId]
    );
  };

  const handleShare = async () => {
    if (navigator.share) {
      try {
        await navigator.share({
          title: `Join Reading Session: ${roomTitle}`,
          text: message,
          url: roomUrl,
        });
      } catch (error) {
        console.log('Share was cancelled');
      }
    } else {
      handleCopyLink();
    }
  };

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto bg-card/95 backdrop-blur-sm border-border/50 dark:bg-card/90 dark:border-border/30">
        <DialogHeader>
          <DialogTitle className="text-foreground dark:text-foreground">
            Invite People to Reading Room
          </DialogTitle>
          <DialogDescription className="text-muted-foreground dark:text-muted-foreground">
            Share "{roomTitle}" with others to join your reading session
          </DialogDescription>
        </DialogHeader>

        {/* Tab Navigation */}
        <div className="flex border-b border-border/30">
          <Button
            variant={activeTab === 'link' ? 'default' : 'ghost'}
            size="sm"
            onClick={() => setActiveTab('link')}
            className="flex-1 rounded-none border-b-2 border-transparent data-[state=active]:border-primary"
          >
            <Link className="h-4 w-4 mr-2" />
            Share Link
          </Button>
          <Button
            variant={activeTab === 'email' ? 'default' : 'ghost'}
            size="sm"
            onClick={() => setActiveTab('email')}
            className="flex-1 rounded-none border-b-2 border-transparent data-[state=active]:border-primary"
          >
            <Mail className="h-4 w-4 mr-2" />
            Email Invite
          </Button>
          <Button
            variant={activeTab === 'contacts' ? 'default' : 'ghost'}
            size="sm"
            onClick={() => setActiveTab('contacts')}
            className="flex-1 rounded-none border-b-2 border-transparent data-[state=active]:border-primary"
          >
            <Users className="h-4 w-4 mr-2" />
            Contacts
          </Button>
        </div>

        <div className="space-y-6">
          {/* Share Link Tab */}
          {activeTab === 'link' && (
            <div className="space-y-4">
              <Card className="bg-background/50 border-border/30">
                <CardContent className="p-4">
                  <Label className="text-foreground dark:text-foreground">Room Link</Label>
                  <div className="flex gap-2 mt-2">
                    <Input
                      value={roomUrl}
                      readOnly
                      className="bg-muted/50"
                    />
                    <Button onClick={handleCopyLink} variant="outline">
                      <Copy className="h-4 w-4" />
                    </Button>
                  </div>
                </CardContent>
              </Card>

              {isPrivate && (
                <div className="p-3 rounded-lg bg-yellow-100 dark:bg-yellow-900/30 border border-yellow-200 dark:border-yellow-800/50">
                  <p className="text-sm text-yellow-800 dark:text-yellow-200">
                    🔒 This is a private room. People will need the room password to join.
                  </p>
                </div>
              )}

              <div className="grid grid-cols-2 gap-3">
                <Button onClick={handleShare} className="w-full">
                  <Share2 className="h-4 w-4 mr-2" />
                  Share via App
                </Button>
                <Button variant="outline" className="w-full">
                  <QrCode className="h-4 w-4 mr-2" />
                  Show QR Code
                </Button>
              </div>
            </div>
          )}

          {/* Email Invite Tab */}
          {activeTab === 'email' && (
            <div className="space-y-4">
              <div>
                <Label htmlFor="emails" className="text-foreground dark:text-foreground">
                  Email Addresses
                </Label>
                <Textarea
                  id="emails"
                  placeholder="Enter email addresses separated by commas&#10;e.g., alice@example.com, bob@example.com"
                  value={emails}
                  onChange={(e) => setEmails(e.target.value)}
                  className="mt-2 min-h-[80px] bg-background/50 border-border/50"
                />
                <p className="text-xs text-muted-foreground mt-1">
                  Separate multiple emails with commas
                </p>
              </div>

              <div>
                <Label htmlFor="message" className="text-foreground dark:text-foreground">
                  Invitation Message
                </Label>
                <Textarea
                  id="message"
                  value={message}
                  onChange={(e) => setMessage(e.target.value)}
                  className="mt-2 bg-background/50 border-border/50"
                  placeholder="Add a personal message..."
                />
              </div>

              <Button 
                onClick={handleSendEmails} 
                disabled={isSending || !emails.trim()}
                className="w-full"
              >
                {isSending ? (
                  <>Sending Invitations...</>
                ) : (
                  <>
                    <Send className="h-4 w-4 mr-2" />
                    Send Email Invitations
                  </>
                )}
              </Button>
            </div>
          )}

          {/* Contacts Tab */}
          {activeTab === 'contacts' && (
            <div className="space-y-4">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                <Input
                  placeholder="Search contacts..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="pl-10 bg-background/50 border-border/50"
                />
              </div>

              <div className="max-h-60 overflow-y-auto space-y-2">
                {filteredContacts.map((contact) => (
                  <Card
                    key={contact.id}
                    className={`cursor-pointer transition-all ${
                      selectedContacts.includes(contact.id)
                        ? 'bg-primary/10 border-primary/50'
                        : 'bg-background/50 border-border/30 hover:bg-muted/50'
                    }`}
                    onClick={() => toggleContact(contact.id)}
                  >
                    <CardContent className="p-3">
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-3">
                          <div className="relative">
                            <Avatar className="h-8 w-8">
                              <AvatarImage src={contact.avatar} />
                              <AvatarFallback>{contact.name[0]}</AvatarFallback>
                            </Avatar>
                            {contact.isOnline && (
                              <div className="absolute -bottom-0.5 -right-0.5 h-3 w-3 bg-green-500 rounded-full border-2 border-background" />
                            )}
                          </div>
                          <div>
                            <p className="text-sm font-medium text-foreground">
                              {contact.name}
                            </p>
                            <p className="text-xs text-muted-foreground">
                              {contact.email}
                            </p>
                          </div>
                        </div>
                        <div className="flex items-center gap-2">
                          {contact.isOnline && (
                            <Badge variant="secondary" className="text-xs">
                              Online
                            </Badge>
                          )}
                          {selectedContacts.includes(contact.id) ? (
                            <Check className="h-4 w-4 text-primary" />
                          ) : (
                            <UserPlus className="h-4 w-4 text-muted-foreground" />
                          )}
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>

              {selectedContacts.length > 0 && (
                <>
                  <Separator />
                  <div className="flex items-center justify-between">
                    <p className="text-sm text-muted-foreground">
                      {selectedContacts.length} contact{selectedContacts.length > 1 ? 's' : ''} selected
                    </p>
                    <Button
                      onClick={handleSendToContacts}
                      disabled={isSending}
                      size="sm"
                    >
                      {isSending ? 'Sending...' : 'Send Invites'}
                    </Button>
                  </div>
                </>
              )}
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="flex justify-end gap-3 pt-4 border-t border-border/30">
          <Button variant="outline" onClick={onClose}>
            Done
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default InvitePeopleModal;