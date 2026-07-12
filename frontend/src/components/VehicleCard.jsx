export default function VehicleCard({
  vehicle,
  isAdmin,
  onPurchase,
  onEdit,
  onDelete,
  restockId,
  restockAmount,
  onRestockStart,
  onRestockAmountChange,
  onRestock,
  onRestockCancel,
}) {
  const inStock = vehicle.quantity > 0;
  const isRestocking = restockId === vehicle.id;

  return (
    <div className="card vehicle-card">
      <div className="vehicle-card-header">
        <div>
          <h3>{vehicle.make} {vehicle.model}</h3>
          <p className="vehicle-meta">{vehicle.category}</p>
        </div>
        <span className={`badge ${inStock ? 'badge-in' : 'badge-out'}`}>
          {inStock ? `${vehicle.quantity} in stock` : 'Out of stock'}
        </span>
      </div>
      <p className="vehicle-price">
        ${Number(vehicle.price).toLocaleString('en-US', { minimumFractionDigits: 2 })}
      </p>
      <div className="vehicle-actions">
        <button
          className="btn btn-success"
          onClick={() => onPurchase(vehicle.id)}
          disabled={!inStock}
        >
          Purchase
        </button>
        {isAdmin && (
          <>
            <button className="btn btn-secondary" onClick={() => onEdit(vehicle)}>
              Edit
            </button>
            <button className="btn btn-danger" onClick={() => onDelete(vehicle.id)}>
              Delete
            </button>
            {isRestocking ? (
              <>
                <input
                  type="number"
                  min="1"
                  value={restockAmount}
                  onChange={(e) => onRestockAmountChange(e.target.value)}
                  placeholder="Qty"
                  style={{ maxWidth: '80px' }}
                />
                <button className="btn" onClick={() => onRestock(vehicle.id)}>Confirm</button>
                <button className="btn btn-secondary" onClick={onRestockCancel}>Cancel</button>
              </>
            ) : (
              <button className="btn btn-secondary" onClick={() => onRestockStart(vehicle.id)}>
                Restock
              </button>
            )}
          </>
        )}
      </div>
    </div>
  );
}
