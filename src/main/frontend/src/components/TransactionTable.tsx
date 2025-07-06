import React from 'react';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { Typography } from '@mui/material';

interface Transaction {
  id: number;
  amount: number;
  category: string;
  description: string;
  timestamp: string;
}

interface TransactionTableProps {
  transactions: Transaction[];
}

const TransactionTable: React.FC<TransactionTableProps> = ({ transactions }) => {
  const columns: GridColDef[] = [
    { field: 'id', headerName: 'ID', width: 90 },
    { field: 'amount', headerName: 'Amount', width: 130, type: 'number' },
    { field: 'category', headerName: 'Category', width: 130 },
    { field: 'description', headerName: 'Description', width: 200 },
    {
      field: 'timestamp',
      headerName: 'Date',
      width: 160,
      valueFormatter: (params) => new Date(params.value).toLocaleString(),
    },
  ];

  return (
    <>
      <Typography variant="h6" gutterBottom>
        Recent Transactions
      </Typography>
      <div style={{ height: 400, width: '100%' }}>
        <DataGrid
          rows={transactions}
          columns={columns}
          pageSizeOptions={[5, 10, 25]}
          initialState={{
            pagination: { paginationModel: { pageSize: 10 } },
          }}
          checkboxSelection
          disableRowSelectionOnClick
        />
      </div>
    </>
  );
};

export default TransactionTable;
