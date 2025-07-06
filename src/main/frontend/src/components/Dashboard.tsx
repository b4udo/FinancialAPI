import React from 'react';
import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import Container from '@mui/material/Container';
import Typography from '@mui/material/Typography';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import TransactionTable from './TransactionTable';
import TransactionSummary from './TransactionSummary';

const Dashboard: React.FC = () => {
  const [transactions, setTransactions] = React.useState<any[]>([]);
  const [loading, setLoading] = React.useState(true);

  React.useEffect(() => {
    fetch('/api/v1/transactions')
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
      })
      .then((data) => {
        console.log('Fetched transactions:', data);
        setTransactions(data);
      })
      .catch((error) => {
        console.error('Error fetching transactions:', error);
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  if (loading) {
    return <Typography>Loading...</Typography>;
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Box
        sx={{
          display: 'grid',
          gap: 3,
          gridTemplateColumns: 'repeat(12, 1fr)',
        }}
      >
        {/* Transaction Summary */}
        <Box gridColumn="span 4">
          <Paper sx={{ p: 2, display: 'flex', flexDirection: 'column' }}>
            <TransactionSummary transactions={transactions} />
          </Paper>
        </Box>

        {/* Spending Trends Chart */}
        <Box gridColumn="span 8">
          <Paper sx={{ p: 2 }}>
            <Typography variant="h6" gutterBottom>
              Spending Trends
            </Typography>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={transactions}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="timestamp" />
                <YAxis />
                <Tooltip />
                <Line type="monotone" dataKey="amount" />
              </LineChart>
            </ResponsiveContainer>
          </Paper>
        </Box>

        {/* Recent Transactions Table */}
        <Box gridColumn="span 12">
          <Paper sx={{ p: 2 }}>
            <TransactionTable transactions={transactions} />
          </Paper>
        </Box>
      </Box>
    </Container>
  );
};

export default Dashboard;