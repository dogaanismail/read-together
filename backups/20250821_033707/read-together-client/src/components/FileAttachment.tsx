import { useState, useRef } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { X, File, Image, Video, Music, FileText, Download } from "lucide-react";

interface AttachedFile {
  id: string;
  name: string;
  size: number;
  type: string;
  url?: string;
}

interface FileAttachmentProps {
  attachedFiles: AttachedFile[];
  onFilesChange: (files: AttachedFile[]) => void;
  onRemoveFile: (fileId: string) => void;
}

const FileAttachment = ({ attachedFiles, onFilesChange, onRemoveFile }: FileAttachmentProps) => {
  const fileInputRef = useRef<HTMLInputElement>(null);

  const getFileIcon = (fileType: string) => {
    if (fileType.startsWith('image/')) return <Image className="h-4 w-4" />;
    if (fileType.startsWith('video/')) return <Video className="h-4 w-4" />;
    if (fileType.startsWith('audio/')) return <Music className="h-4 w-4" />;
    if (fileType.includes('pdf') || fileType.includes('document')) return <FileText className="h-4 w-4" />;
    return <File className="h-4 w-4" />;
  };

  const formatFileSize = (bytes: number) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  const handleFileSelect = (event: React.ChangeEvent<HTMLInputElement>) => {
    const files = event.target.files;
    if (!files) return;

    const newFiles: AttachedFile[] = Array.from(files).map(file => ({
      id: Math.random().toString(36).substring(2, 11),
      name: file.name,
      size: file.size,
      type: file.type,
      url: URL.createObjectURL(file) // For preview purposes
    }));

    onFilesChange([...attachedFiles, ...newFiles]);
    
    // Reset input
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  const handleRemoveFile = (fileId: string) => {
    // Clean up object URL to prevent memory leaks
    const fileToRemove = attachedFiles.find(f => f.id === fileId);
    if (fileToRemove?.url) {
      URL.revokeObjectURL(fileToRemove.url);
    }
    onRemoveFile(fileId);
  };

  return (
    <>
      {/* Hidden file input */}
      <input
        ref={fileInputRef}
        type="file"
        multiple
        onChange={handleFileSelect}
        className="hidden"
        accept="image/*,video/*,audio/*,.pdf,.doc,.docx,.txt,.zip,.rar"
      />

      {/* File attachment button */}
      <Button
        type="button"
        variant="ghost"
        size="sm"
        onClick={() => fileInputRef.current?.click()}
        className="flex-shrink-0"
      >
        <input type="file" className="hidden" />
        📎
      </Button>

      {/* Attached files preview */}
      {attachedFiles.length > 0 && (
        <Card className="absolute bottom-full left-0 right-0 mb-2 bg-card/95 backdrop-blur-sm border-border/50 dark:bg-card/90 dark:border-border/30">
          <CardContent className="p-3">
            <div className="flex items-center justify-between mb-3">
              <h4 className="text-sm font-medium text-foreground dark:text-foreground">
                Attached Files ({attachedFiles.length})
              </h4>
              <Badge variant="outline" className="text-xs">
                {formatFileSize(attachedFiles.reduce((total, file) => total + file.size, 0))}
              </Badge>
            </div>
            
            <div className="space-y-2 max-h-32 overflow-y-auto">
              {attachedFiles.map((file) => (
                <div
                  key={file.id}
                  className="flex items-center gap-3 p-2 rounded-lg bg-muted/50 dark:bg-muted/30"
                >
                  <div className="flex-shrink-0 p-1 rounded bg-background/50 dark:bg-background/30">
                    {getFileIcon(file.type)}
                  </div>
                  
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-foreground dark:text-foreground truncate">
                      {file.name}
                    </p>
                    <p className="text-xs text-muted-foreground dark:text-muted-foreground">
                      {formatFileSize(file.size)}
                    </p>
                  </div>

                  {/* Preview for images */}
                  {file.type.startsWith('image/') && file.url && (
                    <div className="flex-shrink-0">
                      <img
                        src={file.url}
                        alt={file.name}
                        className="h-8 w-8 rounded object-cover"
                      />
                    </div>
                  )}

                  <Button
                    type="button"
                    variant="ghost"
                    size="sm"
                    onClick={() => handleRemoveFile(file.id)}
                    className="flex-shrink-0 h-6 w-6 p-0 text-muted-foreground hover:text-destructive"
                  >
                    <X className="h-3 w-3" />
                  </Button>
                </div>
              ))}
            </div>

            <div className="flex justify-between items-center mt-3 pt-2 border-t border-border/30 dark:border-border/20">
              <Button
                type="button"
                variant="ghost"
                size="sm"
                onClick={() => fileInputRef.current?.click()}
                className="text-xs"
              >
                Add More Files
              </Button>
              
              <div className="flex gap-1 text-xs text-muted-foreground dark:text-muted-foreground">
                <span>Max 10MB per file</span>
              </div>
            </div>
          </CardContent>
        </Card>
      )}
    </>
  );
};

export default FileAttachment;