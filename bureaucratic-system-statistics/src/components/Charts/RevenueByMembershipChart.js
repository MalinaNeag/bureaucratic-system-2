import React from "react";
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from "recharts";

const RevenueByMembershipChart = ({ data }) => {
    return (
        <div className="chart bg-white p-6 rounded-lg shadow-md">
            <h2 className="text-center font-semibold text-lg mb-4">Revenue by Membership</h2>
            <ResponsiveContainer width="100%" height={300}>
                <AreaChart data={data}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="membershipId" />
                    <YAxis />
                    <Tooltip />
                    <Area type="monotone" dataKey="totalRevenue" stroke="#82ca9d" fill="#82ca9d" />
                </AreaChart>
            </ResponsiveContainer>
        </div>
    );
};

export default RevenueByMembershipChart;