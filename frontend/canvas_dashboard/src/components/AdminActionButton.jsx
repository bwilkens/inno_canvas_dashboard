function AdminActionButton({ name, onClick, disabled }) {
  return (
    <button onClick={onClick} disabled={disabled}>
      {name}
    </button>
  );
}

export default AdminActionButton;