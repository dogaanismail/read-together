
import { BookOpen, Heart, Mail, Shield, FileText } from 'lucide-react';
import { Link } from 'react-router-dom';

const Footer = () => {
  return (
    <footer className="bg-gray-900 text-white">
      <div className="max-w-7xl mx-auto px-4 py-12 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          {/* Brand */}
          <div className="col-span-1 md:col-span-2">
            <div className="flex items-center space-x-2 mb-4">
              <div className="flex items-center justify-center w-10 h-10 bg-gradient-to-r from-blue-600 to-teal-600 rounded-lg">
                <BookOpen className="h-6 w-6 text-white" />
              </div>
              <h3 className="text-2xl font-bold bg-gradient-to-r from-blue-400 to-teal-400 bg-clip-text text-transparent">
                ReadTogether
              </h3>
            </div>
            <p className="text-gray-300 mb-4 max-w-md">
              A supportive community where people who stutter can practice reading aloud, connect with others, and track their progress in a safe environment.
            </p>
            <div className="flex items-center space-x-1 text-gray-300">
              <span>Made with</span>
              <Heart className="h-4 w-4 text-red-400" />
              <span>for the stuttering community</span>
            </div>
          </div>

          {/* Quick Links */}
          <div>
            <h4 className="text-lg font-semibold mb-4">Community</h4>
            <ul className="space-y-2">
              <li>
                <Link to="/" className="text-gray-300 hover:text-white transition-colors">
                  Home Feed
                </Link>
              </li>
              <li>
                <Link to="/profile" className="text-gray-300 hover:text-white transition-colors">
                  My Profile
                </Link>
              </li>
              <li>
                <a href="#" className="text-gray-300 hover:text-white transition-colors">
                  Practice Space
                </a>
              </li>
              <li>
                <a href="#" className="text-gray-300 hover:text-white transition-colors">
                  Live Sessions
                </a>
              </li>
            </ul>
          </div>

          {/* Legal */}
          <div>
            <h4 className="text-lg font-semibold mb-4">Legal & Support</h4>
            <ul className="space-y-2">
              <li>
                <Link to="/privacy" className="text-gray-300 hover:text-white transition-colors flex items-center">
                  <Shield className="h-4 w-4 mr-2" />
                  Privacy Policy
                </Link>
              </li>
              <li>
                <Link to="/terms" className="text-gray-300 hover:text-white transition-colors flex items-center">
                  <FileText className="h-4 w-4 mr-2" />
                  Terms of Service
                </Link>
              </li>
              <li>
                <Link to="/contact" className="text-gray-300 hover:text-white transition-colors flex items-center">
                  <Mail className="h-4 w-4 mr-2" />
                  Contact Support
                </Link>
              </li>
              <li>
                <Link to="/guidelines" className="text-gray-300 hover:text-white transition-colors">
                  Community Guidelines
                </Link>
              </li>
              <li>
                <Link to="/feedback" className="text-gray-300 hover:text-white transition-colors">
                  Feature Requests
                </Link>
              </li>
            </ul>
          </div>
        </div>

        {/* Bottom Bar */}
        <div className="border-t border-gray-800 mt-8 pt-8 flex flex-col sm:flex-row justify-between items-center">
          <p className="text-gray-400 text-sm">
            © {new Date().getFullYear()} ReadTogether. All rights reserved.
          </p>
          <div className="flex space-x-6 mt-4 sm:mt-0">
            <Link to="/accessibility" className="text-gray-400 hover:text-white text-sm transition-colors">
              Accessibility
            </Link>
            <Link to="/cookies" className="text-gray-400 hover:text-white text-sm transition-colors">
              Cookies
            </Link>
            <Link to="/help" className="text-gray-400 hover:text-white text-sm transition-colors">
              Help Center
            </Link>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
