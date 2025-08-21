import Navigation from '@/components/Navigation';
import { Mail, MessageSquare, Phone, Clock } from 'lucide-react';

const Contact = () => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-green-50">
      <Navigation />
      
      <div className="max-w-4xl mx-auto px-4 py-8 sm:px-6 lg:px-8">
        <div className="bg-white rounded-lg shadow-sm p-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-8">Contact Support</h1>
          
          <div className="prose prose-lg max-w-none">
            <p className="text-gray-600 mb-8">
              We're here to help! Get in touch with our support team for any questions or assistance.
            </p>

            <div className="grid md:grid-cols-2 gap-8 mb-8">
              <div className="bg-blue-50 p-6 rounded-lg">
                <div className="flex items-center mb-4">
                  <Mail className="h-6 w-6 text-blue-600 mr-3" />
                  <h3 className="text-xl font-semibold text-gray-900">Email Support</h3>
                </div>
                <p className="text-gray-700 mb-2">General inquiries and support</p>
                <a href="mailto:support@readtogether.com" className="text-blue-600 hover:text-blue-800 font-medium">
                  support@readtogether.com
                </a>
              </div>

              <div className="bg-green-50 p-6 rounded-lg">
                <div className="flex items-center mb-4">
                  <MessageSquare className="h-6 w-6 text-green-600 mr-3" />
                  <h3 className="text-xl font-semibold text-gray-900">Community Forum</h3>
                </div>
                <p className="text-gray-700 mb-2">Connect with other community members</p>
                <a href="#" className="text-green-600 hover:text-green-800 font-medium">
                  Visit Community Forum
                </a>
              </div>
            </div>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-gray-900 mb-4">Response Times</h2>
              <div className="flex items-center text-gray-700 mb-2">
                <Clock className="h-5 w-5 mr-2" />
                <span>General Support: 24-48 hours</span>
              </div>
              <div className="flex items-center text-gray-700 mb-2">
                <Clock className="h-5 w-5 mr-2" />
                <span>Technical Issues: 12-24 hours</span>
              </div>
              <div className="flex items-center text-gray-700">
                <Clock className="h-5 w-5 mr-2" />
                <span>Urgent Issues: Within 4 hours</span>
              </div>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-gray-900 mb-4">Before You Contact Us</h2>
              <ul className="list-disc list-inside text-gray-700 space-y-2">
                <li>Check our Help Center for common solutions</li>
                <li>Review our Community Guidelines for community-related questions</li>
                <li>Ensure your browser and device meet our technical requirements</li>
                <li>Have your account information ready when contacting support</li>
              </ul>
            </section>

            <section className="bg-yellow-50 p-6 rounded-lg">
              <h3 className="text-lg font-semibold text-gray-900 mb-2">Emergency Contact</h3>
              <p className="text-gray-700">
                If you're experiencing thoughts of self-harm, please contact your local emergency services or a mental health crisis hotline immediately.
              </p>
            </section>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Contact;