import React, { useState, useEffect } from "react";
import HorizontalBarChart from "../components/Charts/HorizontalBarChart";
import { db } from "../config/firebaseConfig";
import { collection, onSnapshot } from "firebase/firestore";

const BooksPage = () => {
    const [topBooksBorrowed, setTopBooksBorrowed] = useState([]);
    const [topBooksKept, setTopBooksKept] = useState([]);

    useEffect(() => {
        const fetchBookData = () => {
            onSnapshot(collection(db, "borrows"), (snapshot) => {
                const borrowsData = snapshot.docs.map((doc) => doc.data());

                const borrowCounts = borrowsData.reduce((acc, cur) => {
                    acc[cur.bookTitle] = (acc[cur.bookTitle] || 0) + 1;
                    return acc;
                }, {});

                setTopBooksBorrowed(
                    Object.entries(borrowCounts)
                        .map(([bookTitle, count]) => ({ bookTitle, count }))
                        .sort((a, b) => b.count - a.count)
                        .slice(0, 10)
                );

                const readingTimes = borrowsData
                    .filter((borrow) => borrow.returnDate)
                    .map((borrow) => ({
                        bookTitle: borrow.bookTitle,
                        time: Math.ceil(
                            (new Date(borrow.returnDate) - new Date(borrow.borrowDate)) / (1000 * 60 * 60 * 24)
                        ),
                    }));

                const groupedTimes = readingTimes.reduce((acc, cur) => {
                    if (!acc[cur.bookTitle]) acc[cur.bookTitle] = [];
                    acc[cur.bookTitle].push(cur.time);
                    return acc;
                }, {});

                setTopBooksKept(
                    Object.entries(groupedTimes)
                        .map(([bookTitle, times]) => ({ bookTitle, maxTime: Math.max(...times) }))
                        .sort((a, b) => b.maxTime - a.maxTime)
                        .slice(0, 10)
                );
            });
        };

        fetchBookData();
    }, []);

    return (
        <div className="p-6">
            <h1 className="text-3xl font-bold text-center text-[#A87C5A] mb-6">Books Overview</h1>
            <div className="space-y-10">
                <div className="bg-white shadow-lg rounded-lg p-6">
                    <h2 className="text-2xl font-bold text-center mb-4 text-[#A87C5A]">Top 10 Borrowed Books</h2>
                    <HorizontalBarChart data={topBooksBorrowed} xKey="count" yKey="bookTitle" />
                </div>
                <div className="bg-white shadow-lg rounded-lg p-6">
                    <h2 className="text-2xl font-bold text-center mb-4 text-[#A87C5A]">Top 10 Longest Kept Books</h2>
                    <HorizontalBarChart data={topBooksKept} xKey="maxTime" yKey="bookTitle" />
                </div>
            </div>
        </div>
    );
};

export default BooksPage;