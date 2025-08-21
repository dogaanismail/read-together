
import Navigation from '@/components/Navigation';

const Terms = () => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-green-50">
      <Navigation />
      
      <div className="max-w-4xl mx-auto px-4 py-8 sm:px-6 lg:px-8">
        <div className="bg-white rounded-lg shadow-sm p-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-8">Terms of Service</h1>
          
          <div className="prose prose-lg max-w-none">
            <p className="text-gray-600 mb-6">
              Last updated: {new Date().toLocaleDateString()}
            </p>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-gray-900 mb-4">Agreement to Terms</h2>
              <p className="text-gray-700 mb-4">
                By accessing and using ReadTogether, you accept and agree to be bound by the terms and provision of this agreement.
              </p>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-gray-900 mb-4">Community Guidelines</h2>
              <p className="text-gray-700 mb-4">ReadTogether is a supportive community. Users must:</p>
              <ul className="list-disc list-inside text-gray-700 mb-4 space-y-2">
                <li>Be respectful and supportive of all community members</li>
                <li>Not share content that is harmful, offensive, or inappropriate</li>
                <li>Respect intellectual property rights when sharing book content</li>
                <li>Not use the platform for commercial purposes without permission</li>
                <li>Report any violations of community guidelines</li>
              </ul>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-gray-900 mb-4">Content Ownership</h2>
              <p className="text-gray-700 mb-4">
                You retain ownership of the recordings and content you create. By sharing content publicly, you grant ReadTogether a license to display and distribute that content within the platform.
              </p>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-gray-900 mb-4">Privacy and Safety</h2>
              <p className="text-gray-700 mb-4">
                We are committed to maintaining a safe environment for people who stutter. Private practice sessions remain completely private unless you choose to share them.
              </p>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-gray-900 mb-4">Limitation of Liability</h2>
              <p className="text-gray-700 mb-4">
                ReadTogether provides a platform for community support and practice. We are not responsible for individual outcomes and recommend consulting with qualified professionals for educational or therapeutic advice.
              </p>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-gray-900 mb-4">Contact Information</h2>
              <p className="text-gray-700">
                For questions about these Terms of Service, contact us at{' '}
                <a href="mailto:legal@readtogether.com" className="text-blue-600 hover:text-blue-800">
                  legal@readtogether.com
                </a>
              </p>
            </section>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Terms;
