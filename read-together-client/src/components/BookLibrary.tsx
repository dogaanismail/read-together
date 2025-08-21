
import { BookOpen, Clock, TrendingUp } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Progress } from '@/components/ui/progress';

interface Book {
  id: number;
  title: string;
  author: string;
  genre: string;
  sessions: number;
  totalDuration: string;
  lastRead: string;
  progress: number;
  cover: string | null;
}

interface BookLibraryProps {
  books: Book[];
}

const BookLibrary = ({ books }: BookLibraryProps) => {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold text-gray-900 dark:text-gray-100">My Books</h2>
        <Button className="bg-gradient-to-r from-blue-600 to-teal-600 hover:from-blue-700 hover:to-teal-700 dark:from-blue-500 dark:to-teal-500 dark:hover:from-blue-600 dark:hover:to-teal-600 text-white shadow-lg">
          <BookOpen className="h-4 w-4 mr-2" />
          Add Book
        </Button>
      </div>

      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        {books.map((book) => (
          <Card key={book.id} className="overflow-hidden hover:shadow-lg transition-all duration-300 dark:bg-gray-800/50 dark:backdrop-blur-sm dark:border-gray-700/50 dark:shadow-2xl dark:hover:shadow-blue-500/10">
            <div className="p-6">
              <div className="flex items-start space-x-4 mb-4">
                <div className="w-16 h-20 bg-gradient-to-br from-blue-500 to-purple-600 dark:from-blue-400 dark:to-purple-500 rounded-lg flex items-center justify-center flex-shrink-0 shadow-lg">
                  <BookOpen className="h-8 w-8 text-white" />
                </div>
                
                <div className="flex-1 min-w-0">
                  <h3 className="font-semibold text-gray-900 dark:text-gray-100 text-lg mb-1 line-clamp-2">{book.title}</h3>
                  <p className="text-gray-600 dark:text-gray-300 text-sm mb-2">by {book.author}</p>
                  <Badge variant="outline" className="text-xs dark:border-gray-600 dark:text-gray-300 dark:bg-gray-700/30">{book.genre}</Badge>
                </div>
              </div>

              <div className="space-y-3 mb-4">
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600 dark:text-gray-400">Progress</span>
                  <span className="font-medium text-gray-900 dark:text-gray-100">{book.progress}%</span>
                </div>
                <Progress value={book.progress} className="h-2" />
              </div>

              <div className="grid grid-cols-2 gap-4 mb-4 text-sm">
                <div>
                  <div className="text-gray-600 dark:text-gray-400">Sessions</div>
                  <div className="font-medium text-gray-900 dark:text-gray-100">{book.sessions}</div>
                </div>
                <div>
                  <div className="text-gray-600 dark:text-gray-400">Total Time</div>
                  <div className="font-medium text-gray-900 dark:text-gray-100">{book.totalDuration}</div>
                </div>
              </div>

              <div className="text-xs text-gray-500 dark:text-gray-400 mb-4">
                Last read: {new Date(book.lastRead).toLocaleDateString()}
              </div>

              <div className="flex space-x-2">
                <Button 
                  size="sm" 
                  className="flex-1 bg-gradient-to-r from-blue-600 to-teal-600 hover:from-blue-700 hover:to-teal-700 dark:from-blue-500 dark:to-teal-500 dark:hover:from-blue-600 dark:hover:to-teal-600 text-white font-medium shadow-lg"
                >
                  Continue Reading
                </Button>
                <Button 
                  variant="outline" 
                  size="sm" 
                  className="px-3 border-green-200 text-green-700 hover:bg-green-50 hover:text-green-800 hover:border-green-300 dark:border-green-700 dark:text-green-300 dark:hover:bg-green-900/20 dark:hover:text-green-200 dark:hover:border-green-600 font-medium"
                >
                  <TrendingUp className="h-4 w-4" />
                </Button>
              </div>
            </div>
          </Card>
        ))}
      </div>

      {books.length === 0 && (
        <Card className="p-12 text-center dark:bg-gray-800/50 dark:backdrop-blur-sm dark:border-gray-700/50 dark:shadow-2xl">
          <div className="max-w-md mx-auto">
            <div className="flex items-center justify-center w-20 h-20 bg-blue-100 dark:bg-blue-900/50 rounded-full mx-auto mb-6">
              <BookOpen className="h-10 w-10 text-blue-600 dark:text-blue-400" />
            </div>
            <h3 className="text-xl font-bold text-gray-900 dark:text-gray-100 mb-4">No Books Yet</h3>
            <p className="text-gray-600 dark:text-gray-300 mb-6">
              Start building your reading library by adding your first book.
            </p>
            <Button className="bg-gradient-to-r from-blue-600 to-teal-600 hover:from-blue-700 hover:to-teal-700 dark:from-blue-500 dark:to-teal-500 dark:hover:from-blue-600 dark:hover:to-teal-600 text-white shadow-lg">
              <BookOpen className="h-4 w-4 mr-2" />
              Add Your First Book
            </Button>
          </div>
        </Card>
      )}
    </div>
  );
};

export default BookLibrary;
