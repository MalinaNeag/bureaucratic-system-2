import React from "react";
import { Link } from "react-router-dom";

const WelcomePage = () => (
    <div className="flex flex-col items-center justify-center h-screen text-center bg-gradient-to-br from-purple-600 via-pink-500 to-yellow-400 text-white">
        {/* Icon */}
        <img
            src="/images-removebg-preview.png"
            alt="Books Icon"
            className="w-48 h-48 mb-8 animate-bounce"
        />

        {/* Title */}
        <h1 className="text-5xl font-extrabold mb-2">Welcome to</h1>
        <h1 className="text-6xl font-extrabold text-yellow-200 drop-shadow-lg mb-6">
            Bureaucratic Library System
        </h1>

        {/* Subtitle */}
        <p className="text-lg font-medium mb-10">
            Select a category below to explore the statistics!
        </p>

        {/* Buttons */}
        <div className="flex space-x-8">
            <Link
                to="/users"
                className="bg-blue-500 hover:bg-blue-600 text-white font-bold py-3 px-8 rounded-lg shadow-lg transform transition-transform duration-300 hover:scale-110"
            >
                Users
            </Link>
            <Link
                to="/books"
                className="bg-green-500 hover:bg-green-600 text-white font-bold py-3 px-8 rounded-lg shadow-lg transform transition-transform duration-300 hover:scale-110"
            >
                Books
            </Link>
            <Link
                to="/revenue"
                className="bg-red-500 hover:bg-red-600 text-white font-bold py-3 px-8 rounded-lg shadow-lg transform transition-transform duration-300 hover:scale-110"
            >
                Revenue
            </Link>
        </div>
        <div className="bg-test">Test Tailwind</div>
    </div>
);

export default WelcomePage;