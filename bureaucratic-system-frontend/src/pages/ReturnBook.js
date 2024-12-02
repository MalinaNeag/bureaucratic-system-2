import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from '../components/AuthProvider.js'; // Import the Auth Context
import { getFirestore, collection, query, where, getDocs } from 'firebase/firestore'; // Firestore

const ReturnRequest = () => {
    const { user, role } = useAuth(); // Get the user and role from AuthContext
    const [membershipId, setMembershipId] = useState('');
    const [bookTitle, setBookTitle] = useState('');
    const [bookAuthor, setBookAuthor] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const db = getFirestore();
    useEffect(() => {
        const fetchMembershipId = async () => {
            try {
                if (!user) {
                    setError('You are not logged in.');
                    setLoading(false);
                    return;
                }

                // Fetch membership ID based on the logged-in user's citizen ID
                const userEmail = user.email;

                // Step 1: Get the citizen's ID from the `users` collection
                const usersCollection = collection(db, 'users');
                const userQuery = query(usersCollection, where('email', '==', userEmail));
                const userSnapshot = await getDocs(userQuery);

                if (!userSnapshot.empty) {
                    const userData = userSnapshot.docs[0].data();
                    const citizenId = userData.id;

                    // Step 2: Fetch the membership ID from the `memberships` collection using the citizen ID
                    const membershipsCollection = collection(db, 'memberships');
                    const membershipQuery = query(membershipsCollection, where('citizenId', '==', citizenId));
                    const membershipSnapshot = await getDocs(membershipQuery);

                    if (!membershipSnapshot.empty) {
                        const membershipData = membershipSnapshot.docs[0].data();
                        setMembershipId(() => membershipData.id); // Set the membership ID
                    } else {
                        setError('Membership profile not found.');
                    }
                } else {
                    setError('Citizen profile not found.');
                }
            } catch (err) {
                setError('Error fetching membership ID: ' + err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchMembershipId();
    }, [db, user]);
    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!role || role !== 'citizen') {
            alert('You are not authorized to submit a return request.');
            return;
        }

        try {
            const response = await axios.post(
                'http://localhost:8080/api/returns/return-book',
                {
                    membershipId,
                    bookTitle,
                    bookAuthor,
                },
                {
                    headers: {
                        Authorization: `Bearer ${role}`, // Use the role as the token
                    },
                }
            );
            alert('Return processed: ' + response.data);
        }catch (err) {
            if (err.response) {
                // Server responded with a status code other than 2xx
                console.error('Server Response Error:', err.response.data);
                alert(`Error: ${err.response.data.message || err.response.data || 'Unknown error'}`);
            } else if (err.request) {
                // Request was made but no response was received
                console.error('Request Error:', err.request);
                alert('No response received from the server.');
            } else {
                // Other errors
                console.error('Error:', err.message);
                alert(`Error: ${err.message}`);
            }
        }
    };

    if (loading) {
        return <p className="text-center text-gray-600">Loading...</p>;
    }

    if (error) {
        return <p className="text-center text-red-500">{error}</p>;
    }

    return (
        <div
            className="flex flex-col items-center justify-center min-h-screen"
            style={{
                background: 'linear-gradient(to bottom, white, #A87C5A)',
            }}
        >
            <div className="bg-white rounded-lg shadow-lg p-8 w-full max-w-2xl">
                <h1 className="text-2xl font-bold text-gray-800 mb-6 text-center">
                    Return Request
                </h1>
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700">
                            Membership ID
                        </label>
                        <input
                            type="text"
                            value={membershipId}
                            readOnly
                            className="block w-full p-2 border border-gray-300 rounded shadow-sm bg-gray-100 cursor-not-allowed"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">
                            Book Title
                        </label>
                        <input
                            type="text"
                            placeholder="Enter book title"
                            value={bookTitle}
                            onChange={(e) => setBookTitle(e.target.value)}
                            required
                            className="block w-full p-2 border border-gray-300 rounded shadow-sm"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">
                            Book Author
                        </label>
                        <input
                            type="text"
                            placeholder="Enter book author"
                            value={bookAuthor}
                            onChange={(e) => setBookAuthor(e.target.value)}
                            required
                            className="block w-full p-2 border border-gray-300 rounded shadow-sm"
                        />
                    </div>
                    <button
                        type="submit"
                        className="w-full bg-[#A87C5A] text-white font-semibold py-2 rounded shadow-md hover:bg-[#8B5E3C]"
                    >
                        Submit Return
                    </button>
                </form>
            </div>
        </div>
    );
};

export default ReturnRequest;