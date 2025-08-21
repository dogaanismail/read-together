import Navigation from '@/components/Navigation';
import { Users, Shield, Heart, AlertTriangle } from 'lucide-react';

const Guidelines = () => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-green-50">
      <Navigation />
      
      <div className="max-w-4xl mx-auto px-4 py-8 sm:px-6 lg:px-8">
        <div className="bg-white rounded-lg shadow-sm p-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-8">Community Guidelines</h1>
          
          <div className="prose prose-lg max-w-none">
            <p className="text-gray-600 mb-8">
              Our community guidelines help create a safe, supportive environment where everyone can practice and grow together.
            </p>

            <section className="mb-8">
              <div className="flex items-center mb-4">
                <Heart className="h-6 w-6 text-red-500 mr-3" />
                <h2 className="text-2xl font-bold text-gray-900">Be Kind and Supportive</h2>
              </div>
              <ul className="list-disc list-inside text-gray-700 space-y-2">
                <li>Encourage fellow community members in their speech journey</li>
                <li>Celebrate progress, no matter how small</li>
                <li>Offer constructive feedback when requested</li>
                <li>Remember that everyone is at a different stage in their journey</li>
                <li>Use inclusive language that makes everyone feel welcome</li>
              </ul>
            </section>

            <section className="mb-8">
              <div className="flex items-center mb-4">
                <Shield className="h-6 w-6 text-blue-500 mr-3" />
                <h2 className="text-2xl font-bold text-gray-900">Respect Privacy and Boundaries</h2>
              </div>
              <ul className="list-disc list-inside text-gray-700 space-y-2">
                <li>Never share someone else's recordings without permission</li>
                <li>Respect when someone chooses to keep sessions private</li>
                <li>Don't pressure others to share more than they're comfortable with</li>
                <li>Keep personal information shared in the community confidential</li>
                <li>Ask before offering advice about speech therapy techniques</li>
              </ul>
            </section>

            <section className="mb-8">
              <div className="flex items-center mb-4">
                <Users className="h-6 w-6 text-green-500 mr-3" />
                <h2 className="text-2xl font-bold text-gray-900">Content Standards</h2>
              </div>
              <ul className="list-disc list-inside text-gray-700 space-y-2">
                <li>Keep all content appropriate and family-friendly</li>
                <li>No hate speech, discrimination, or bullying of any kind</li>
                <li>Respect copyright when sharing book passages</li>
                <li>No spam, promotional content, or commercial solicitation</li>
                <li>Share content that adds value to the community</li>
              </ul>
            </section>

            <section className="mb-8">
              <div className="flex items-center mb-4">
                <AlertTriangle className="h-6 w-6 text-yellow-500 mr-3" />
                <h2 className="text-2xl font-bold text-gray-900">Prohibited Content</h2>
              </div>
              <div className="bg-red-50 p-4 rounded-lg mb-4">
                <p className="text-gray-700 font-medium mb-2">The following content is strictly prohibited:</p>
                <ul className="list-disc list-inside text-gray-700 space-y-1">
                  <li>Harassment, threats, or intimidation</li>
                  <li>Discriminatory language based on stuttering severity or other characteristics</li>
                  <li>Explicit, violent, or inappropriate material</li>
                  <li>Personal attacks or inflammatory comments</li>
                  <li>Misinformation about speech therapy or medical advice</li>
                </ul>
              </div>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-gray-900 mb-4">Reporting and Enforcement</h2>
              <p className="text-gray-700 mb-4">
                If you encounter content or behavior that violates these guidelines, please report it immediately. 
                We take all reports seriously and will investigate promptly.
              </p>
              <div className="bg-blue-50 p-4 rounded-lg">
                <h3 className="font-semibold text-gray-900 mb-2">Enforcement Actions May Include:</h3>
                <ul className="list-disc list-inside text-gray-700 space-y-1">
                  <li>Content removal</li>
                  <li>Temporary account suspension</li>
                  <li>Permanent account termination</li>
                  <li>Restriction from community features</li>
                </ul>
              </div>
            </section>

            <section className="bg-green-50 p-6 rounded-lg">
              <h3 className="text-lg font-semibold text-gray-900 mb-2">Remember</h3>
              <p className="text-gray-700">
                We're all here to support each other in our speech journeys. By following these guidelines, 
                you help create a positive environment where everyone can feel safe to practice, share, and grow.
              </p>
            </section>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Guidelines;