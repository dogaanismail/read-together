
import { useState, useRef } from 'react';
import { X, Mic, MicOff, Video, VideoOff, Play, Square, Upload, BookOpen, File, Trash2 } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Textarea } from '@/components/ui/textarea';
import { Switch } from '@/components/ui/switch';
import { Badge } from '@/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Card } from '@/components/ui/card';

interface RecordingModalProps {
  isOpen: boolean;
  onClose: () => void;
}

const RecordingModal = ({ isOpen, onClose }: RecordingModalProps) => {
  const [activeTab, setActiveTab] = useState('record');
  const [recordingType, setRecordingType] = useState<'video' | 'audio'>('audio');
  const [isRecording, setIsRecording] = useState(false);
  const [isPaused, setIsPaused] = useState(false);
  const [recordingTime, setRecordingTime] = useState('00:00');
  const [isPrivate, setIsPrivate] = useState(true);
  const [sessionTitle, setSessionTitle] = useState('');
  const [selectedBook, setSelectedBook] = useState('');
  const [selectedLanguage, setSelectedLanguage] = useState('');
  const [description, setDescription] = useState('');
  const [uploadedFile, setUploadedFile] = useState<File | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const languages = ['English', 'Turkish', 'Spanish', 'French', 'German', 'Other'];
  const popularBooks = [
    'The Alchemist',
    'Harry Potter Series',
    'To Kill a Mockingbird',
    'Pride and Prejudice',
    'The Great Gatsby',
    'Custom/Other'
  ];

  const handleStartRecording = () => {
    setIsRecording(true);
    setIsPaused(false);
    // Start recording timer simulation
    let seconds = 0;
    const timer = setInterval(() => {
      if (!isPaused) {
        seconds++;
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        setRecordingTime(`${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`);
      }
    }, 1000);
  };

  const handleStopRecording = () => {
    setIsRecording(false);
    setIsPaused(false);
  };

  const handlePauseRecording = () => {
    setIsPaused(!isPaused);
  };

  const handleFileUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      // Validate file type
      const isAudio = file.type.startsWith('audio/');
      const isVideo = file.type.startsWith('video/');
      
      if (isAudio || isVideo) {
        setUploadedFile(file);
        setRecordingType(isVideo ? 'video' : 'audio');
      } else {
        alert('Please upload a valid audio or video file.');
      }
    }
  };

  const handleRemoveFile = () => {
    setUploadedFile(null);
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  const formatFileSize = (bytes: number) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  const formatDuration = (file: File) => {
    // In a real app, you'd use a library to get actual duration
    return "Duration will be calculated";
  };

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="text-2xl font-bold text-gray-900 flex items-center">
            <BookOpen className="h-6 w-6 mr-2 text-blue-600" />
            Create Reading Session
          </DialogTitle>
        </DialogHeader>

        <Tabs value={activeTab} onValueChange={setActiveTab} className="space-y-6">
          <TabsList className="grid w-full grid-cols-2">
            <TabsTrigger value="record">Record Live</TabsTrigger>
            <TabsTrigger value="upload">Upload File</TabsTrigger>
          </TabsList>

          {/* Session Details - Common for both tabs */}
          <div className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="title">Session Title</Label>
                <Input
                  id="title"
                  placeholder="e.g., Reading Chapter 3 of The Alchemist"
                  value={sessionTitle}
                  onChange={(e) => setSessionTitle(e.target.value)}
                />
              </div>

              <div className="space-y-2">
                <Label>Language</Label>
                <Select value={selectedLanguage} onValueChange={setSelectedLanguage}>
                  <SelectTrigger>
                    <SelectValue placeholder="Select language" />
                  </SelectTrigger>
                  <SelectContent>
                    {languages.map((lang) => (
                      <SelectItem key={lang} value={lang.toLowerCase()}>
                        {lang}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
            </div>

            <div className="space-y-2">
              <Label>Book/Content</Label>
              <Select value={selectedBook} onValueChange={setSelectedBook}>
                <SelectTrigger>
                  <SelectValue placeholder="Select a book or content" />
                </SelectTrigger>
                <SelectContent>
                  {popularBooks.map((book) => (
                    <SelectItem key={book} value={book.toLowerCase()}>
                      {book}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-2">
              <Label htmlFor="description">Description (Optional)</Label>
              <Textarea
                id="description"
                placeholder="Add any notes about your reading session..."
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                rows={3}
              />
            </div>

            {/* Privacy Settings */}
            <div className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
              <div className="space-y-1">
                <Label className="text-base font-medium">Privacy Setting</Label>
                <p className="text-sm text-gray-600">
                  {isPrivate ? 'Only you can see this session' : 'Community members can see this session'}
                </p>
              </div>
              <div className="flex items-center space-x-3">
                <span className="text-sm font-medium">Public</span>
                <Switch
                  checked={isPrivate}
                  onCheckedChange={setIsPrivate}
                />
                <span className="text-sm font-medium">Private</span>
              </div>
            </div>
          </div>

          <TabsContent value="record" className="space-y-6">
            {/* Recording Type Selection */}
            <div className="space-y-3">
              <Label className="text-base font-semibold">Recording Type</Label>
              <div className="flex gap-3">
                <Button
                  type="button"
                  variant={recordingType === 'audio' ? 'default' : 'outline'}
                  onClick={() => setRecordingType('audio')}
                  className="flex-1"
                >
                  <Mic className="h-4 w-4 mr-2" />
                  Audio Only
                </Button>
                <Button
                  type="button"
                  variant={recordingType === 'video' ? 'default' : 'outline'}
                  onClick={() => setRecordingType('video')}
                  className="flex-1"
                >
                  <Video className="h-4 w-4 mr-2" />
                  Video & Audio
                </Button>
              </div>
            </div>

            {/* Recording Controls */}
            <div className="space-y-4">
              <div className="flex items-center justify-center p-8 bg-gradient-to-br from-blue-50 to-teal-50 rounded-lg border-2 border-dashed border-blue-200">
                {!isRecording ? (
                  <div className="text-center">
                    <div className="flex items-center justify-center w-20 h-20 bg-blue-600 rounded-full mx-auto mb-4 hover:bg-blue-700 transition-colors cursor-pointer"
                         onClick={handleStartRecording}>
                      {recordingType === 'video' ? (
                        <Video className="h-10 w-10 text-white" />
                      ) : (
                        <Mic className="h-10 w-10 text-white" />
                      )}
                    </div>
                    <p className="text-lg font-semibold text-gray-900 mb-2">Ready to Record</p>
                    <p className="text-gray-600">Click the button above to start your {recordingType} session</p>
                  </div>
                ) : (
                  <div className="text-center w-full">
                    <div className="flex items-center justify-center space-x-4 mb-4">
                      <Badge className="bg-red-500 text-white px-4 py-2 text-lg animate-pulse">
                        🔴 Recording {recordingTime}
                      </Badge>
                    </div>
                    
                    <div className="flex justify-center space-x-3">
                      <Button
                        variant="outline"
                        onClick={handlePauseRecording}
                        className="border-yellow-500 text-yellow-600 hover:bg-yellow-50"
                      >
                        {isPaused ? <Play className="h-4 w-4 mr-2" /> : <Square className="h-4 w-4 mr-2" />}
                        {isPaused ? 'Resume' : 'Pause'}
                      </Button>
                      
                      <Button
                        variant="destructive"
                        onClick={handleStopRecording}
                      >
                        <Square className="h-4 w-4 mr-2" />
                        Stop Recording
                      </Button>
                    </div>
                  </div>
                )}
              </div>
            </div>
          </TabsContent>

          <TabsContent value="upload" className="space-y-6">
            {/* File Upload Area */}
            <Card className="p-8">
              <div className="text-center">
                <div className="mb-6">
                  <div className="flex items-center justify-center w-20 h-20 bg-blue-100 rounded-full mx-auto mb-4">
                    <Upload className="h-10 w-10 text-blue-600" />
                  </div>
                  <h3 className="text-xl font-semibold text-gray-900 mb-2">Upload Your Recording</h3>
                  <p className="text-gray-600 mb-6">
                    Upload a pre-recorded audio or video file of your reading session
                  </p>
                </div>

                {!uploadedFile ? (
                  <div className="space-y-4">
                    <div className="border-2 border-dashed border-gray-300 rounded-lg p-8 hover:border-blue-400 transition-colors">
                      <input
                        ref={fileInputRef}
                        type="file"
                        accept="audio/*,video/*"
                        onChange={handleFileUpload}
                        className="hidden"
                        id="file-upload"
                      />
                      <label htmlFor="file-upload" className="cursor-pointer">
                        <div className="space-y-2">
                          <File className="h-12 w-12 text-gray-400 mx-auto" />
                          <p className="text-lg font-medium text-gray-900">Drop your file here or click to browse</p>
                          <p className="text-sm text-gray-500">Supports MP3, WAV, MP4, MOV, and more</p>
                        </div>
                      </label>
                    </div>
                    
                    <Button 
                      onClick={() => fileInputRef.current?.click()}
                      className="bg-blue-600 hover:bg-blue-700"
                    >
                      <Upload className="h-4 w-4 mr-2" />
                      Choose File
                    </Button>
                  </div>
                ) : (
                  <div className="space-y-4">
                    <div className="bg-green-50 border border-green-200 rounded-lg p-6">
                      <div className="flex items-start justify-between">
                        <div className="flex items-center space-x-3">
                          <div className="flex items-center justify-center w-10 h-10 bg-green-100 rounded-lg">
                            {uploadedFile.type.startsWith('video/') ? (
                              <Video className="h-5 w-5 text-green-600" />
                            ) : (
                              <Mic className="h-5 w-5 text-green-600" />
                            )}
                          </div>
                          <div className="text-left">
                            <p className="font-medium text-gray-900">{uploadedFile.name}</p>
                            <p className="text-sm text-gray-600">
                              {formatFileSize(uploadedFile.size)} • {uploadedFile.type.startsWith('video/') ? 'Video' : 'Audio'}
                            </p>
                          </div>
                        </div>
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={handleRemoveFile}
                          className="text-red-600 hover:text-red-700 hover:bg-red-50"
                        >
                          <Trash2 className="h-4 w-4" />
                        </Button>
                      </div>
                    </div>

                    <div className="flex gap-2">
                      <Button 
                        variant="outline"
                        onClick={() => fileInputRef.current?.click()}
                        className="flex-1"
                      >
                        <Upload className="h-4 w-4 mr-2" />
                        Choose Different File
                      </Button>
                    </div>
                  </div>
                )}
              </div>
            </Card>

            {/* Goodreads Integration Suggestion */}
            <Card className="p-6 bg-gradient-to-r from-green-50 to-blue-50 border-green-200">
              <div className="flex items-start space-x-3">
                <BookOpen className="h-6 w-6 text-green-600 mt-1" />
                <div>
                  <h4 className="font-semibold text-gray-900 mb-2">💡 Coming Soon: Goodreads Integration</h4>
                  <p className="text-sm text-gray-700 mb-3">
                    We're working on integrating with Goodreads to automatically sync your reading list, 
                    track progress, and discover new books recommended by the community.
                  </p>
                  <Badge variant="outline" className="bg-white text-green-700 border-green-300">
                    Feature in Development
                  </Badge>
                </div>
              </div>
            </Card>
          </TabsContent>

          {/* Action Buttons */}
          <div className="flex justify-end space-x-3 pt-4 border-t">
            <Button variant="outline" onClick={onClose}>
              Cancel
            </Button>
            <Button 
              className="bg-gradient-to-r from-blue-600 to-teal-600 hover:from-blue-700 hover:to-teal-700"
              disabled={!sessionTitle || !selectedLanguage || !selectedBook || (activeTab === 'upload' && !uploadedFile)}
            >
              <Upload className="h-4 w-4 mr-2" />
              {activeTab === 'upload' ? 'Upload Session' : 'Save Session'}
            </Button>
          </div>
        </Tabs>
      </DialogContent>
    </Dialog>
  );
};

export default RecordingModal;
