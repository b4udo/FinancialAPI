import React from 'react';
import { DataGrid, GridColDef } from '@mui/x-data-grid';

interface TransactionTableProps {
  transactions: any[];
}

const TransactionTable: React.FC<TransactionTableProps> = ({ transactions }) => {
  const columns: GridColDef[] = [
    { field: 'id', headerName: 'ID', width: 90 },
    {
      field: 'timestamp',
      headerName: 'Date',
      width: 160,
      valueFormatter: (params: any) => new Date(params.value).toLocaleString(),
    },
    { field: 'description', headerName: 'Description', width: 150 },
    { field: 'amount', headerName: 'Amount', width: 110 },
  ];

  return (
    <div style={{ height: 400, width: '100%' }}>
      <DataGrid
        rows={transactions}
        columns={columns}
        initialState={{ pagination: { paginationModel: { pageSize: 5 } } }}
        pageSizeOptions={[5]}
        getRowId={(row) => row.id}
      />
    </div>
  );
};

export default TransactionTable;
