INSERT INTO agricultural_documents(title, content, category, source, created_at)
VALUES
    ('Wheat Rust Control',
     'Wheat rust (Puccinia) is controlled by Propiconazole(Tilt 25 EC) at 1 ml/litre. Spray at flag-leaf stage. Repeat after 15 days if needed',
     'pest-control',
     'ICAR Wheat Disease Management Guide',
     NOW()
    ),
    (
     'Drip Irrigation for Cotton',
     'Cotton needs 600-700 mm water/season. Drip irrigation saved 30-40%. Space laterals 60 cm apart. Irrigate at 0.8 ETC.',
     'irrigation',
     'TNAU Agritech Portal',
     NOW()
    ),
    (
     'Soil pH Correction',
     'If soil ph < 6.0, apply 2-4 tonnes lime/hectare before sowing. Use Balanced NPK per soil health card.',
     'soil-health',
     'Government of India Soil Health Card Scheme',
     NOW()
    ),
    (
     'Paddy Blast Disease',
     'Rice blast (Magnaporthe oryzae) causes leaf/neck lesions. Apply Tricyclazole 75 WP at 0.6 g/litre at tillering. Avoid excess nitrogen',
     'pest-control',
     'ICAR Rice Knowledge Management Portal',
     NOW()
    );