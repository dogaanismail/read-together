import { Bell } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';

interface NotificationBadgeProps {
  count: number;
  onClick: () => void;
}

const NotificationBadge = ({ count, onClick }: NotificationBadgeProps) => {
  return (
    <Button variant="ghost" size="sm" className="relative" onClick={onClick}>
      <Bell className="h-5 w-5" />
      {count > 0 && (
        <Badge 
          className="absolute -top-1 -right-1 bg-red-500 text-white text-xs min-w-[18px] h-[18px] flex items-center justify-center rounded-full p-0"
        >
          {count > 99 ? '99+' : count}
        </Badge>
      )}
    </Button>
  );
};

export default NotificationBadge;