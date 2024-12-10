import React from "react";
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from "recharts";

const MedianReadingTimeChart = ({ data }) => {
    return (
        <div className="chart bg-white p-6 rounded-lg shadow-md">
            <h2 className="text-center font-semibold text-lg mb-4">Median Time to Read a Book</h2>
            <ResponsiveContainer width="100%" height={300}>
                <BarChart data={data}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="bookTitle" />
                    <YAxis />
                    <Tooltip />
                    <Bar dataKey="medianTime" fill="#8884d8" />
                </BarChart>
            </ResponsiveContainer>
        </div>
    );
};

export default MedianReadingTimeChart;