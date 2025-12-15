import React, { useState } from 'react';
import { postToService } from '../lib/apiClient';

interface ServiceFormProps {
  title: string;
  description: string;
  serviceKey: 'user' | 'product' | 'order';
}

export const ServiceForm: React.FC<ServiceFormProps> = ({ title, description, serviceKey }) => {
  const [body, setBody] = useState<string>(
    serviceKey === 'user'
      ? '{"userId": "u-1", "name": "Alice"}'
      : serviceKey === 'product'
      ? '{"productId": "p-1", "name": "Widget"}'
      : '{"orderId": "o-1", "userId": "u-1", "productId": "p-1", "quantity": 1}',
  );
  const [status, setStatus] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setStatus(null);
    try {
      const res = await postToService(serviceKey, body);
      setStatus(`HTTP ${res.status}`);
    } catch (err: any) {
      setError(err?.message ?? 'Unknown error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="card">
      <h2>{title}</h2>
      <p className="card-description">{description}</p>
      <form onSubmit={onSubmit} className="form">
        <label className="form-label">
          JSON body
          <textarea
            className="form-textarea"
            value={body}
            onChange={(e) => setBody(e.target.value)}
            rows={6}
          />
        </label>
        <button type="submit" className="btn" disabled={loading}>
          {loading ? 'Sending…' : 'Send'}
        </button>
      </form>

      <div className="status">
        {status && <span className="status-ok">{status}</span>}
        {error && <span className="status-error">{error}</span>}
      </div>
    </section>
  );
};
