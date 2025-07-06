import React from 'react';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import IconButton from '@mui/material/IconButton';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';

interface TransactionTableProps {
  transactions: any[];
}

const TransactionTable: React.FC<TransactionTableProps> = ({ transactions }) => {
  const handleEdit = (id: any) => console.log(`Edit transaction ${id}`);
  const handleDelete = (id: any) => console.log(`Delete transaction ${id}`);

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
    {
      field: 'actions',
      headerName: 'Actions',
      width: 120,
      sortable: false,
      filterable: false,
      renderCell: (params: any) => (
        <>
          <IconButton size="small" onClick={() => handleEdit(params.id)}>
            <EditIcon fontSize="inherit" />
          </IconButton>
          <IconButton size="small" onClick={() => handleDelete(params.id)}>
            <DeleteIcon fontSize="inherit" />
          </IconButton>
        </>
      ),
    },
  ];

  return (
    <div style={{ height: 450, width: '100%' }}>
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