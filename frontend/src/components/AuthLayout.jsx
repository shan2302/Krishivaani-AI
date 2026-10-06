export default function AuthLayout({ title, subtitle, children }) {
    return (
        <div className="min-vh-100 d-flex align-items-center justify-content-center py-4"
            style={{
                background: 'linear-gradient(135deg,#1a472a 0%, #2d6a4f 50%, #52b788 100%)',
            }}
        >
            <div className="card shadow-lg border-0 mx-3 mx-sm-0"
                style={{ width: '100%', maxWidth: '440px', borderRadius: '16px' }}>
                <div className="card-body p-4 p-sm-5">
                    <div className="text-center mb-4">
                        <div className="d-inline-flex align-items-center justify-content-center rounded-circle mb-3"
                            style={{ width: 64, height: 64, background: 'linear-gradient(135deg,#1a472a,#52b788)' }}
                        >
                            <span style={{ fontSize: 28 }}>🌾</span>
                        </div>
                        <h2 className="fw-bold mb-1" style={{ color: '#1a472a' }}>KrishiVaani AI</h2>
                        <h5 className="fw-semibold mb-1">{title}</h5>
                        <p className="text-muted small">{subtitle}</p>
                    </div>
                    {children}
                </div>
            </div>
        </div>
    );
}