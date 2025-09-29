import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import LoanToolServices from '../Services/LoanToolsServices';
import { Box, Typography, Paper, Stack, IconButton, Drawer, List, ListItem, ListItemButton, ListItemIcon, ListItemText, Button, MenuItem, Select, InputLabel, FormControl, TextField } from "@mui/material";
import MenuIcon from "@mui/icons-material/Menu";
import HomeIcon from "@mui/icons-material/Home";
import BuildIcon from "@mui/icons-material/Build";
import AddBoxIcon from "@mui/icons-material/AddBox";
import ToolServices from '../Services/ToolServices';
import ClientServices from '../Services/ClientServices';
import KardexServices from '../Services/KardexServices';
import { useKeycloak } from '@react-keycloak/web';
import LibraryAddIcon from "@mui/icons-material/LibraryAdd"; // 📚➕ Para Agregar Herramienta
import AssessmentIcon from "@mui/icons-material/Assessment"; // 📊 Para Ver Kardex (reporte
import ContactsIcon from "@mui/icons-material/Contacts";     // 📇 Para Clientes)
import PersonAddAltIcon from '@mui/icons-material/PersonAddAlt';
import AdminPanelSettingsIcon from "@mui/icons-material/AdminPanelSettings";
import ReportIcon from '@mui/icons-material/Report'; // 📈 Para Reportes

const LoanTool = () => {
    const navigate = useNavigate();
    const { id } = useParams(); // ID del cliente desde la URL
    const [toollist, settoollist] = useState([]);
    const [clientlist, setclientlist] = useState([]);
    const [selectedTool, setSelectedTool] = useState("");
    const [selectedClient, setSelectedClient] = useState(id || "");
    const [drawerOpen, setDrawerOpen] = useState(false);
    const [startDate, setStartDate] = useState("");
    const [returnDate, setReturnDate] = useState("");
    const [kardex, setKardex] = useState([]);
    const { keycloak } = useKeycloak();
    const isAdmin = keycloak.hasRealmRole('ADMIN');

    useEffect(() => {
        ToolServices.getAll()
            .then(response => {
                const availableTools = response.data.filter(tool => tool.states === 1);
                settoollist(availableTools);
            })
            .catch(error => {
                console.error("Error fetching tool list:", error);
            });

        ClientServices.getAll()
            .then(response => {
                setclientlist(response.data);
            })
            .catch(error => {
                console.error("Error fetching client list:", error);
            });
    }, []);

    const handleLoan = () => {
        if (!selectedTool || !selectedClient || !startDate || !returnDate) {
            alert("Selecciona una herramienta, un cliente y ambas fechas.");
            return;
        }

        const clienteObj = clientlist.find(c => String(c.id) === String(selectedClient));
        const emailclient = clienteObj ? clienteObj.email : "unknown";

        LoanToolServices.create({
            toolid: selectedTool,
            clientid: selectedClient,
            initiallenddate: startDate,
            finalreturndate: returnDate
        })
        .then(() => {
            alert("Herramienta prestada correctamente.");
            KardexServices.create({
                idtool: selectedTool,
                username: emailclient,
                date: new Date(),
                stateToolsId: 2,
                quantity: 1
            });
            navigate("/Home");
        })
        .catch(error => {
            alert("Error al prestar herramienta.");
            console.error(error);
        });
    };

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
    

    return (
        <>
            {/* Fondo pantalla completa */}
            <Box
                sx={{
                    position: "fixed",
                    top: 0,
                    left: 0,
                    width: "100vw",
                    height: "100vh",
                    backgroundColor: "#FEF3E2 ",
                    zIndex: -1
                }}
            />
            <Box sx={{ p: 4, minHeight: "100vh" }}>
                <IconButton
                    color="inherit"
                    onClick={() => setDrawerOpen(true)}
                    sx={{ position: "fixed", top: 16, left: 16, zIndex: 10, backgroundColor: "#FA812F", boxShadow: 3 , '&:hover': { backgroundColor: "#FA812F" }}}
                >
                    <MenuIcon />
                </IconButton>
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
                <Paper sx={{ maxWidth: 500, mx: "auto", p: 4, mt: 6, borderRadius: 4, boxShadow: 6 }}>
                    <Typography variant="h5" align="center" gutterBottom sx={{ fontWeight: "bold", color: "rgba(255, 94, 0, 1)" }}>
                        Prestar Herramienta
                    </Typography>
                    <Stack spacing={3}>
                        <FormControl fullWidth>
                            <InputLabel id="tool-select-label">Herramienta</InputLabel>
                            <Select
                                labelId="tool-select-label"
                                value={selectedTool}
                                label="Herramienta"
                                onChange={(e) => setSelectedTool(e.target.value)}
                            >
                                {toollist.map(tool => (
                                    <MenuItem key={tool.id} value={tool.id}>{tool.name}</MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                        <FormControl fullWidth>
                            <InputLabel id="client-select-label">Cliente</InputLabel>
                            <Select
                                labelId="client-select-label"
                                value={selectedClient}
                                label="Cliente"
                                onChange={(e) => setSelectedClient(e.target.value)}
                            >
                                {clientlist.map(client => (
                                    <MenuItem key={client.id} value={client.id}>{client.name} ({client.rut})</MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                        <TextField
                            label="Fecha inicio préstamo"
                            type="date"
                            value={startDate}
                            onChange={(e) => setStartDate(e.target.value)}
                            InputLabelProps={{ shrink: true }}
                            fullWidth
                            inputProps={{ min: new Date().toISOString().split("T")[0] }}
                            sx={{
                                '& input[type="date"]::-webkit-calendar-picker-indicator': {
                                    backgroundImage: `url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 20 20' fill='%23ff5e00'%3e%3cpath fill-rule='evenodd' d='M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z' clip-rule='evenodd'/%3e%3c/svg%3e")`,
                                    backgroundRepeat: 'no-repeat',
                                    backgroundPosition: 'center',
                                    backgroundSize: '16px',
                                    cursor: 'pointer',
                                    width: '20px',
                                    height: '20px'
                                }
                            }}

                        />
                        <TextField
                            label="Fecha retorno herramienta"
                            type="date"
                            value={returnDate}
                            onChange={(e) => setReturnDate(e.target.value)}
                            InputLabelProps={{ shrink: true }}
                            fullWidth
                            inputProps={{ min: new Date().toISOString().split("T")[0] }}
                            sx={{
                                '& input[type="date"]::-webkit-calendar-picker-indicator': {
                                    backgroundImage: `url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 20 20' fill='%23ff5e00'%3e%3cpath fill-rule='evenodd' d='M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z' clip-rule='evenodd'/%3e%3c/svg%3e")`,
                                    backgroundRepeat: 'no-repeat',
                                    backgroundPosition: 'center',
                                    backgroundSize: '16px',
                                    cursor: 'pointer',
                                    width: '20px',
                                    height: '20px'
                                }
                            }}
                        />
                        <Button
                            variant="contained"
                            color="primary"
                            onClick={handleLoan}
                            sx={{ backgroundColor: "rgba(255, 94, 0, 1)" }}
                            disabled={!selectedTool || !selectedClient || !startDate || !returnDate}
                        >
                            Prestar Herramienta
                        </Button>
                        <Button
                            variant="outlined"
                            color="secondary"
                            onClick={() => navigate("/ToolList")}
                        >
                            Cancelar
                        </Button>
                    </Stack>
                </Paper>
            </Box>
        </>
    );
};

export default LoanTool;