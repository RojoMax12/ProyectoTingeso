import { useState } from 'react';
import { TextField, Button, Box, Typography, Paper, Stack, Drawer, List, ListItem, ListItemButton, ListItemIcon, ListItemText, IconButton } from "@mui/material";
import { useNavigate } from 'react-router-dom';
import HomeIcon from "@mui/icons-material/Home";
import BuildIcon from "@mui/icons-material/Build";
import MenuIcon from "@mui/icons-material/Menu";
import ClientServices from '../Services/ClientServices';  
import { useKeycloak } from '@react-keycloak/web'; 
import LibraryAddIcon from "@mui/icons-material/LibraryAdd"; // 📚➕ Para Agregar Herramienta
import AssessmentIcon from "@mui/icons-material/Assessment"; // 📊 Para Ver Kardex (reporte
import ContactsIcon from "@mui/icons-material/Contacts";     // 📇 Para Clientes)
import PersonAddAltIcon from '@mui/icons-material/PersonAddAlt';
import AdminPanelSettingsIcon from "@mui/icons-material/AdminPanelSettings"; // 🛡️⚙️ Para Configuraciones (admin)
import ReportIcon from '@mui/icons-material/Report'; // 📈 Para Reportes

const RegisterClient = () => {
    const navigate = useNavigate();
    const [drawerOpen, setDrawerOpen] = useState(false);
    const [name, setName] = useState("");
    const [rut, setRut] = useState("");
    const [email, setEmail] = useState("");
    const [phone, setPhone] = useState("");
    const { keycloak } = useKeycloak();
    const isAdmin = keycloak?.tokenParsed?.realm_access?.roles?.includes("ADMIN");

   const sidebarOptions = [
  { text: "Inicio", icon: <HomeIcon />, path: "/" },
  { text: "Herramientas", icon: <BuildIcon />, path: "/ToolList" },
  { text: "Agregar Herramienta", icon: <LibraryAddIcon />, path: "/AddTools" },
  { text: "Ver Kardex", icon: <AssessmentIcon />, path: "/Kardex" },
  { text: "Registrar Cliente", icon: <PersonAddAltIcon />, path: "/RegisterClient" },
  { text: "Clientes", icon: <ContactsIcon />, path: "/ClientList" },
  { text: "Reportes", icon: <ReportIcon />, path: "/Reports" },
  // Solo mostrar Configuraciones si es admin
  ...(isAdmin ? [{ text: "Configuraciones", icon: <AdminPanelSettingsIcon />, path: "/Configuration" }] : [])
];

    const formatRut = (value) => {
        if (!value) return "";
        const [rut, dv] = value.replace(/\./g, "").split("-");
        if (!rut || !dv) return value; // Si falta el dígito verificador, no formatear
        if (rut.length < 7) return value; // Si el rut es muy corto, no formatear
        return `${rut.slice(0, 2)}.${rut.slice(2, 5)}.${rut.slice(5)}-${dv}`;
    };

    const formatnumber = (value) => {
    // Elimina todo excepto dígitos
        const digits = value.replace(/\D/g, '').slice(0, 8); // Limita a 8 dígitos después del 9
        return digits ? `+569${digits}` : '';
    };

    const handleSave = () => {
        const newClient = {
            name,
            rut: formatRut(rut),
            email,
            phone
        };
        ClientServices.create(newClient)
            .then(() => {
                alert("Cliente registrado");
            })
            .catch(error => {
                console.error("Error al registrar cliente:", error);
            });
    };

    return (
        <>
            {/* Botón para abrir la barra lateral */}
            <IconButton
                color="inherit"
                onClick={() => setDrawerOpen(true)}
                sx={{ position: "fixed", top: 16, left: 16, zIndex: 10, backgroundColor: "#FA812F", boxShadow: 3 , '&:hover': { backgroundColor: "#FA812F" }}}
            >
                <MenuIcon />
            </IconButton>

            {/* Barra lateral temporal */}
            <Drawer
                open={drawerOpen}
                onClose={() => setDrawerOpen(false)}
                variant="temporary"
                sx={{
                    [`& .MuiDrawer-paper`]: { width: 220, boxSizing: "border-box", backgroundColor: "#FEF3E2" }
                }}
            >
                <List>
                    {sidebarOptions.map((option) => (
                        <ListItem key={option.text} disablePadding>
                            <ListItemButton onClick={() => { navigate(option.path); setDrawerOpen(false); }}>
                                <ListItemIcon sx={{ color: "#FA812F" }}>{option.icon}</ListItemIcon>
                                <ListItemText primary={option.text} />
                            </ListItemButton>
                        </ListItem>
                    ))}
                </List>
            </Drawer>

            {/* Fondo que cubre toda la pantalla */}
            <Box
                sx={{
                    position: "fixed",
                    top: 0,
                    left: 0,
                    width: "100vw",
                    height: "100vh",
                    backgroundColor: "#FEF3E2",
                    zIndex: -1
                }}
            />

            {/* Contenedor principal con cuadro destacado */}
            <Box sx={{ minHeight: "100vh", backgroundColor: "#FEF3E2", py: 4, display: "flex", alignItems: "center" }}>
                <Box
                    sx={{
                        maxWidth: 500,
                        mx: "auto",
                        mt: 4,
                        backgroundColor: "#fff8f0",
                        borderRadius: 4,
                        boxShadow: 6,
                        p: 4,
                        border: "2px solid rgba(255, 94, 0, 0.2)"
                    }}
                >
                    <Typography variant="h4" align="center" gutterBottom sx={{ fontWeight: "bold", color: "rgba(255, 94, 0, 1)" }}>
                        Registrar Cliente
                    </Typography>
                    <Paper sx={{ p: 4, boxShadow: 3, backgroundColor: "transparent", elevation: 0 }}>
                        <Stack spacing={3}>
                            <TextField label="Nombre del Cliente" variant="outlined" fullWidth onChange={(e) => setName(e.target.value)} />
                            <TextField label="Rut" variant="outlined" fullWidth value={rut} onChange={(e) => setRut(formatRut(e.target.value))} />
                            <TextField label="Correo Electrónico" variant="outlined" fullWidth onChange={(e) => setEmail(e.target.value)} />
                            <TextField label="Número de Teléfono" variant="outlined" fullWidth onChange={(e) => setPhone(formatnumber(e.target.value))} />
                            <Button type="submit" variant="contained" color="primary" sx={{ backgroundColor: "rgba(255, 94, 0, 1)" }} onClick={handleSave}>
                                Registrar
                            </Button>
                        </Stack>
                    </Paper>
                </Box>
            </Box>
        </>
    );
};

export default RegisterClient;