import React from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';

interface CategorySummary {
  name: string;
  amount: number;
}

interface TransactionSummaryProps {
  transactions: any[];
}

const TransactionSummary: React.FC<TransactionSummaryProps> = ({ transactions }) => {
  const categorySummary: CategorySummary[] = [
    { name: 'Food', amount: 120 },
    { name: 'Transport', amount: 60 },
    { name: 'Utilities', amount: 200 },
    { name: 'Entertainment', amount: 150 },
  ];

  return (
    <>
      <Typography variant="h6">Summary by Category</Typography>
      <Box sx={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: 1, mt: 2 }}>
        {categorySummary.map((category) => (
          <Box key={category.name} sx={{ display: 'flex', alignItems: 'center' }}>
            <Box sx={{ flexGrow: 1 }}>
              <Typography variant="body2">{category.name}</Typography>
            </Box>
            <Typography variant="body2">€{category.amount}</Typography>
          </Box>
        ))}
      </Box>
    </>
  );
};

export default TransactionSummary;