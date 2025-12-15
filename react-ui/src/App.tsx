import React from 'react';
import { ServiceForm } from './components/ServiceForm';

const App: React.FC = () => {
  return (
    <div className="app-root">
      <header className="app-header">
        <h1>Orders Enrichment Demo UI</h1>
        <p>Send JSON events to User, Product, and Order services.</p>
      </header>

      <main className="app-main">
        <ServiceForm
          title="User Service"
          description="POST to /users on port 8081"
          serviceKey="user"
        />
        <ServiceForm
          title="Product Service"
          description="POST to /products on port 8082"
          serviceKey="product"
        />
        <ServiceForm
          title="Order Service"
          description="POST to /orders on port 8083"
          serviceKey="order"
        />
      </main>
    </div>
  );
};

export default App;
