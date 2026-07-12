import { useState, useEffect, useCallback } from 'react';
import { api } from '../api';
import { useAuth } from '../context/AuthContext';
import VehicleCard from '../components/VehicleCard';

const emptyForm = { make: '', model: '', category: '', price: '', quantity: '' };
const emptySearch = { make: '', model: '', category: '', minPrice: '', maxPrice: '' };

export default function Dashboard() {
  const { isAdmin } = useAuth();
  const [vehicles, setVehicles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [search, setSearch] = useState(emptySearch);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [restockId, setRestockId] = useState(null);
  const [restockAmount, setRestockAmount] = useState('');

  const loadVehicles = useCallback(async (searchParams = null) => {
    setLoading(true);
    setError('');
    try {
      const hasSearch = searchParams && Object.values(searchParams).some((v) => v !== '');
      const data = hasSearch
        ? await api.searchVehicles(searchParams)
        : await api.getVehicles();
      setVehicles(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadVehicles();
  }, [loadVehicles]);

  const handleSearch = (e) => {
    e.preventDefault();
    loadVehicles(search);
  };

  const handleClearSearch = () => {
    setSearch(emptySearch);
    loadVehicles();
  };

  const handleFormSubmit = async (e) => {
    e.preventDefault();
    const payload = {
      make: form.make,
      model: form.model,
      category: form.category,
      price: parseFloat(form.price),
      quantity: parseInt(form.quantity, 10),
    };
    try {
      if (editingId) {
        await api.updateVehicle(editingId, payload);
      } else {
        await api.createVehicle(payload);
      }
      setForm(emptyForm);
      setEditingId(null);
      loadVehicles(search);
    } catch (err) {
      setError(err.message);
    }
  };

  const handleEdit = (vehicle) => {
    setEditingId(vehicle.id);
    setForm({
      make: vehicle.make,
      model: vehicle.model,
      category: vehicle.category,
      price: String(vehicle.price),
      quantity: String(vehicle.quantity),
    });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this vehicle?')) return;
    try {
      await api.deleteVehicle(id);
      loadVehicles(search);
    } catch (err) {
      setError(err.message);
    }
  };

  const handlePurchase = async (id) => {
    try {
      await api.purchaseVehicle(id);
      loadVehicles(search);
    } catch (err) {
      setError(err.message);
    }
  };

  const handleRestock = async (id) => {
    const amount = parseInt(restockAmount, 10);
    if (!amount || amount < 1) return;
    try {
      await api.restockVehicle(id, amount);
      setRestockId(null);
      setRestockAmount('');
      loadVehicles(search);
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="container">
      <div className="dashboard-header">
        <h1>Vehicle Inventory</h1>
        <p style={{ color: 'var(--text-muted)' }}>
          Browse, search, and purchase from our dealership stock
        </p>
      </div>

      {error && <p className="error" style={{ marginBottom: '1rem' }}>{error}</p>}

      <form className="search-bar" onSubmit={handleSearch}>
        <div className="form-group" style={{ margin: 0 }}>
          <label>Make</label>
          <input value={search.make} onChange={(e) => setSearch({ ...search, make: e.target.value })} placeholder="Toyota" />
        </div>
        <div className="form-group" style={{ margin: 0 }}>
          <label>Model</label>
          <input value={search.model} onChange={(e) => setSearch({ ...search, model: e.target.value })} placeholder="Camry" />
        </div>
        <div className="form-group" style={{ margin: 0 }}>
          <label>Category</label>
          <input value={search.category} onChange={(e) => setSearch({ ...search, category: e.target.value })} placeholder="SUV" />
        </div>
        <div className="form-group" style={{ margin: 0 }}>
          <label>Min Price</label>
          <input type="number" value={search.minPrice} onChange={(e) => setSearch({ ...search, minPrice: e.target.value })} placeholder="0" />
        </div>
        <div className="form-group" style={{ margin: 0 }}>
          <label>Max Price</label>
          <input type="number" value={search.maxPrice} onChange={(e) => setSearch({ ...search, maxPrice: e.target.value })} placeholder="100000" />
        </div>
        <button type="submit" className="btn">Search</button>
        <button type="button" className="btn btn-secondary" onClick={handleClearSearch}>Clear</button>
      </form>

      {isAdmin && (
        <div className="admin-panel">
          <h2>{editingId ? 'Edit Vehicle' : 'Add New Vehicle'}</h2>
          <form className="admin-form" onSubmit={handleFormSubmit}>
            <div className="form-group" style={{ margin: 0 }}>
              <label>Make</label>
              <input value={form.make} onChange={(e) => setForm({ ...form, make: e.target.value })} required />
            </div>
            <div className="form-group" style={{ margin: 0 }}>
              <label>Model</label>
              <input value={form.model} onChange={(e) => setForm({ ...form, model: e.target.value })} required />
            </div>
            <div className="form-group" style={{ margin: 0 }}>
              <label>Category</label>
              <input value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })} required />
            </div>
            <div className="form-group" style={{ margin: 0 }}>
              <label>Price</label>
              <input type="number" step="0.01" value={form.price} onChange={(e) => setForm({ ...form, price: e.target.value })} required />
            </div>
            <div className="form-group" style={{ margin: 0 }}>
              <label>Quantity</label>
              <input type="number" value={form.quantity} onChange={(e) => setForm({ ...form, quantity: e.target.value })} required />
            </div>
            <button type="submit" className="btn">{editingId ? 'Update' : 'Add Vehicle'}</button>
            {editingId && (
              <button type="button" className="btn btn-secondary" onClick={() => { setEditingId(null); setForm(emptyForm); }}>
                Cancel
              </button>
            )}
          </form>
        </div>
      )}

      {loading ? (
        <p className="empty-state">Loading vehicles...</p>
      ) : vehicles.length === 0 ? (
        <p className="empty-state">No vehicles found. Try adjusting your search.</p>
      ) : (
        <div className="vehicle-grid">
          {vehicles.map((vehicle) => (
            <VehicleCard
              key={vehicle.id}
              vehicle={vehicle}
              isAdmin={isAdmin}
              onPurchase={handlePurchase}
              onEdit={handleEdit}
              onDelete={handleDelete}
              restockId={restockId}
              restockAmount={restockAmount}
              onRestockStart={(id) => { setRestockId(id); setRestockAmount(''); }}
              onRestockAmountChange={setRestockAmount}
              onRestock={handleRestock}
              onRestockCancel={() => { setRestockId(null); setRestockAmount(''); }}
            />
          ))}
        </div>
      )}
    </div>
  );
}
