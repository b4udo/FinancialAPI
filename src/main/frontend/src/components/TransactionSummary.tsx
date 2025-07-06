import React from 'react';
import { Typography, Box, Grid } from '@mui/material';
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip } from 'recharts';

interface Transaction {
  amount: number;
  category: string;
}

interface TransactionSummaryProps {
  transactions: Transaction[];
}

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884D8'];

const TransactionSummary: React.FC<TransactionSummaryProps> = ({ transactions }) => {
  const calculateCategorySummary = () => {
    const summary = transactions.reduce((acc, curr) => {
      acc[curr.category] = (acc[curr.category] || 0) + curr.amount;
      return acc;
    }, {} as Record<string, number>);

    return Object.entries(summary).map(([category, amount]) => ({
      name: category,
      value: amount
    }));
  };

  const totalSpent = transactions.reduce((sum, curr) => sum + curr.amount, 0);
  const categorySummary = calculateCategorySummary();

  return (
    <>
      <Typography variant="h6" gutterBottom>
        Spending Summary
      </Typography>
      <Box sx={{ mt: 2 }}>
        <Typography variant="h4" align="center" gutterBottom>
          ${totalSpent.toFixed(2)}
        </Typography>
        <Typography variant="subtitle2" align="center" color="textSecondary">
          Total Spent
        </Typography>
      </Box>

      <Box sx={{ height: 200, mt: 2 }}>
        <ResponsiveContainer width="100%" height="100%">
          <PieChart>
            <Pie
              data={categorySummary}
              dataKey="value"
              nameKey="name"
              cx="50%"
              cy="50%"
              outerRadius={80}
              label
            >
              {categorySummary.map((entry, index) => (
                <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
              ))}
            </Pie>
            <Tooltip />
          </PieChart>
        </ResponsiveContainer>
      </Box>

      <Grid container spacing={1} sx={{ mt: 2 }}>
        {categorySummary.map((category, index) => (
          <Grid item xs={6} key={category.name}>
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <Box
                sx={{
                  width: 12,
                  height: 12,
                  backgroundColor: COLORS[index % COLORS.length],
                  mr: 1
                }}
              />
              <Typography variant="body2">
                {category.name}: ${category.value.toFixed(2)}
              </Typography>
            </Box>
          </Grid>
        ))}
      </Grid>
    </>
  );
};

export default TransactionSummary;
