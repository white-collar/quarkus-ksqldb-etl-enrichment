type ServiceKey = 'user' | 'product' | 'order';

const DEFAULT_ENDPOINTS: Record<ServiceKey, string> = {
  user: 'http://localhost:8081/users',
  product: 'http://localhost:8082/products',
  order: 'http://localhost:8083/orders',
};

const ENDPOINTS: Record<ServiceKey, string> = {
  user: import.meta.env.VITE_USER_SERVICE_URL ?? DEFAULT_ENDPOINTS.user,
  product: import.meta.env.VITE_PRODUCT_SERVICE_URL ?? DEFAULT_ENDPOINTS.product,
  order: import.meta.env.VITE_ORDER_SERVICE_URL ?? DEFAULT_ENDPOINTS.order,
};

export async function postToService(service: ServiceKey, body: string): Promise<Response> {
  let parsed: any;
  try {
    parsed = JSON.parse(body);
  } catch (e) {
    throw new Error('Body must be valid JSON');
  }

  const res = await fetch(ENDPOINTS[service], {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(parsed),
  });

  if (!res.ok && res.status >= 400) {
    const text = await res.text().catch(() => '');
    throw new Error(`Request failed with status ${res.status}${text ? `: ${text}` : ''}`);
  }

  return res;
}
