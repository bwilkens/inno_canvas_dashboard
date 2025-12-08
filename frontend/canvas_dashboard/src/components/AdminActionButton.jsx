import "../css/admin-button.css";

function AdminActionButton({ name, onClick, disabled }) {
  return (
    <button class="admin-button" onClick={onClick} disabled={disabled}>
      {name}
    </button>
  );
}

export default AdminActionButton;