import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";

interface EmojiPickerProps {
  onEmojiSelect: (emoji: string) => void;
  onClose: () => void;
}

const EmojiPicker = ({ onEmojiSelect, onClose }: EmojiPickerProps) => {
  const emojiCategories = {
    "Smileys & People": [
      "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😊", "😇",
      "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘", "😗", "😙", "😚",
      "😋", "😛", "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎", "🤩",
      "🥳", "😏", "😒", "😞", "😔", "😟", "😕", "🙁", "☹️", "😣",
      "😖", "😫", "😩", "🥺", "😢", "😭", "😤", "😠", "😡", "🤬"
    ],
    "Animals & Nature": [
      "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐻‍❄️", "🐨",
      "🐯", "🦁", "🐮", "🐷", "🐽", "🐸", "🐵", "🙈", "🙉", "🙊",
      "🐒", "🐔", "🐧", "🐦", "🐤", "🐣", "🐥", "🦆", "🦅", "🦉",
      "🦇", "🐺", "🐗", "🐴", "🦄", "🐝", "🐛", "🦋", "🐌", "🐞"
    ],
    "Food & Drink": [
      "🍎", "🍏", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🫐", "🍈",
      "🍒", "🍑", "🥭", "🍍", "🥥", "🥝", "🍅", "🍆", "🥑", "🥦",
      "🥬", "🥒", "🌶️", "🫑", "🌽", "🥕", "🧄", "🧅", "🥔", "🍠",
      "🥐", "🍞", "🥖", "🥨", "🧀", "🥚", "🍳", "🧈", "🥞", "🧇"
    ],
    "Activities": [
      "⚽", "🏀", "🏈", "⚾", "🥎", "🎾", "🏐", "🏉", "🥏", "🎱",
      "🪀", "🏓", "🏸", "🏒", "🏑", "🥍", "🏏", "🪃", "🥅", "⛳",
      "🪁", "🏹", "🎣", "🤿", "🥊", "🥋", "🎽", "🛹", "🛼", "🛷",
      "⛸️", "🥌", "🎿", "⛷️", "🏂", "🪂", "🏋️‍♀️", "🏋️‍♂️", "🤼‍♀️", "🤼‍♂️"
    ],
    "Objects": [
      "⌚", "📱", "📲", "💻", "⌨️", "🖥️", "🖨️", "🖱️", "🖲️", "🕹️",
      "🗜️", "💽", "💾", "💿", "📀", "📼", "📷", "📸", "📹", "🎥",
      "📽️", "🎞️", "📞", "☎️", "📟", "📠", "📺", "📻", "🎙️", "🎚️",
      "🎛️", "🧭", "⏱️", "⏲️", "⏰", "🕰️", "⏳", "⌛", "📡", "🔋"
    ],
    "Hearts": [
      "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔",
      "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝", "💟", "☮️",
      "✝️", "☪️", "🕉️", "☸️", "✡️", "🔯", "🕎", "☯️", "☦️", "🛐"
    ]
  };

  const [selectedCategory, setSelectedCategory] = useState<string>("Smileys & People");

  const handleEmojiClick = (emoji: string) => {
    onEmojiSelect(emoji);
    onClose();
  };

  return (
    <Card className="absolute bottom-full right-0 mb-2 w-80 shadow-lg bg-card/95 backdrop-blur-sm border-border/50 dark:bg-card/90 dark:border-border/30">
      <CardContent className="p-0">
        {/* Category Tabs */}
        <div className="flex overflow-x-auto border-b border-border/50 dark:border-border/30">
          {Object.keys(emojiCategories).map((category) => (
            <Button
              key={category}
              variant={selectedCategory === category ? "default" : "ghost"}
              size="sm"
              onClick={() => setSelectedCategory(category)}
              className="flex-shrink-0 text-xs rounded-none border-r border-border/30 dark:border-border/20"
            >
              {category.split(" ")[0]}
            </Button>
          ))}
        </div>

        {/* Emoji Grid */}
        <div className="p-3 h-48 overflow-y-auto">
          <div className="grid grid-cols-8 gap-1">
            {emojiCategories[selectedCategory as keyof typeof emojiCategories].map((emoji, index) => (
              <Button
                key={index}
                variant="ghost"
                size="sm"
                onClick={() => handleEmojiClick(emoji)}
                className="h-8 w-8 p-0 text-lg hover:bg-muted/50 dark:hover:bg-muted/30"
              >
                {emoji}
              </Button>
            ))}
          </div>
        </div>

        {/* Recently used section */}
        <div className="border-t border-border/50 dark:border-border/30 p-2">
          <p className="text-xs text-muted-foreground dark:text-muted-foreground mb-2">Recently used:</p>
          <div className="flex gap-1">
            {["😀", "❤️", "👍", "😂", "🔥", "💯", "😍", "👏"].map((emoji, index) => (
              <Button
                key={index}
                variant="ghost"
                size="sm"
                onClick={() => handleEmojiClick(emoji)}
                className="h-8 w-8 p-0 text-lg hover:bg-muted/50 dark:hover:bg-muted/30"
              >
                {emoji}
              </Button>
            ))}
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

export default EmojiPicker;