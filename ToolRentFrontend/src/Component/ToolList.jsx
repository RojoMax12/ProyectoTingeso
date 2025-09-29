import { useState, useEffect } from "react";
import { TableContainer, Paper, Table, TableHead, TableRow, TableCell, TableBody, Button, Typography, Box, Drawer, List, ListItem, ListItemButton, ListItemIcon, ListItemText, IconButton } from "@mui/material";
import ToolServices from "../Services/ToolServices";
import { useNavigate } from "react-router";
import HomeIcon from "@mui/icons-material/Home";
import BuildIcon from "@mui/icons-material/Build";
import MenuIcon from "@mui/icons-material/Menu";
import StateToolServices from "../Services/StateToolsServices";
import kardexServices from "../Services/KardexServices";
import { useKeycloak } from "@react-keycloak/web";
import LibraryAddIcon from "@mui/icons-material/LibraryAdd"; // 📚➕ Para Agregar Herramienta
import AssessmentIcon from "@mui/icons-material/Assessment"; // 📊 Para Ver Kardex (reporte
import ContactsIcon from "@mui/icons-material/Contacts";     // 📇 Para Clientes)
import PersonAddAltIcon from '@mui/icons-material/PersonAddAlt';
import AdminPanelSettingsIcon from "@mui/icons-material/AdminPanelSettings"; // 🛡️⚙️ Para Configuraciones (admin)
import ReportIcon from '@mui/icons-material/Report'; // 📈 Para Reportes


const ToolList = () => {
    const [tools, setTools] = useState([]);
    const [drawerOpen, setDrawerOpen] = useState(false); // Estado para mostrar/ocultar la barra lateral
    const navigate = useNavigate();
    const [stateNames, setStateNames] = useState({});
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

    const handleGoToAddTool = () => {
        navigate("/AddTools");
    };

    const handleEdit = (id) => {
        navigate(`/EditTools/${id}`);
    };


    const handleDelete = (id) => {
    if (window.confirm("¿Seguro que quieres eliminar esta herramienta?")) {
        ToolServices.deleteid(id)
                .then(() => {
                    fetchTools(); // Actualiza la lista desde el backend
                })
                .catch(error => {
                    console.error("Error al eliminar herramienta:", error);
                });
        }
    };

    const handleDisarget = (idtool) => {
        if (window.confirm("¿Seguro que quieres dar de baja esta herramienta?")) {
            ToolServices.unsuscribeTools(idtool)
                .then(() => {
                    fetchTools(); // Actualiza la lista desde el backend
                    kardexServices.create({
                        date: new Date(),
                        stateToolsId: 4,
                        username: keycloak.tokenParsed.preferred_username,
                        quantity: 1,
                        idtool: idtool
                    });
                    alert("Herramienta dada de baja");
                })
                .catch(error => {
                    console.error("Error al dar de baja herramienta:", error);
                });
        }
    };

    const fetchTools = () => {
    ToolServices.getAll()
        .then((response) => {
            setTools(response.data);
            const uniqueStateIds = [...new Set(response.data.map(tool => tool.states))].filter(id => id != null);
            Promise.all(uniqueStateIds.map(id =>
                StateToolServices.getid(id)
                    .then(res => ({ id, name: res.data.name }))
                    .catch(() => ({ id, name: "Desconocido" }))
            )).then(results => {
                const namesObj = {};
                results.forEach(({ id, name }) => {
                    namesObj[id] = name;
                });
                setStateNames(namesObj);
            });
        })
        .catch((error) => {
            console.error("There was an error!", error);
        });
};

    useEffect(() => {
    fetchTools();
}, []);

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

            {/* Contenido principal sin margen izquierdo */}
            <Box sx={{
                minHeight: "100vh",
                backgroundColor: "#FEF3E2",
                py: 4,
                display: "flex",
                alignItems: "center",
                pt: 8
            }}>
                <Box
                    sx={{
                        maxWidth: 900,
                        mx: "auto",
                        mt: 4,
                        backgroundColor: "#fff8f0",
                        borderRadius: 4,
                        boxShadow: 6,
                        p: 4,
                        border: "2px solid rgba(255, 94, 0, 0.2)",
                        width: '100%'
                    }}
                >
                    <Typography variant="h4" align="center" gutterBottom sx={{ fontWeight: "bold", mb: 4, color: "rgba(255, 94, 0, 1)" }}>
                        Lista de Herramientas
                    </Typography>
                    <TableContainer component={Paper} sx={{ boxShadow: 3 }}>
                        <Table>
                            <TableHead>
                                <TableRow sx={{ backgroundColor: "rgba(255, 94, 0, 1)" }}>
                                    <TableCell sx={{ color: "#fff" }}>ID</TableCell>
                                    <TableCell sx={{ color: "#fff" }}>Nombre</TableCell>
                                    <TableCell sx={{ color: "#fff" }}>Categoría</TableCell>
                                    <TableCell sx={{ color: "#fff" }}>Costo Reemplazo</TableCell>
                                    <TableCell sx={{ color: "#fff" }}>Estado</TableCell>
                                    <TableCell sx={{ color: "#fff" }}>Acciones</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {tools.map((tool) => (
                                    <TableRow key={tool.id}>
                                        <TableCell>{tool.id}</TableCell>
                                        <TableCell>{tool.name}</TableCell>
                                        <TableCell>{tool.category}</TableCell>
                                        <TableCell>{tool.replacement_cost}</TableCell>
                                        <TableCell>{stateNames[tool.states] || "Cargando..."}</TableCell>
                                        <TableCell>
                                            <Button
                                                variant="outlined"
                                                color="primary"
                                                size="small"
                                                sx={{ mr: 1 }}
                                                onClick={() => handleEdit(tool.id)}
                                            >
                                                Editar
                                            </Button>
                                            <Button
                                                variant="outlined"
                                                color="error"
                                                size="small"
                                                onClick={() => handleDelete(tool.id)}
                                            >
                                                Eliminar
                                            </Button>
                                            {isAdmin && (
                                                <Button
                                                    variant="outlined"
                                                    color="info"
                                                    size="small"
                                                    onClick={() => handleDisarget(tool.id)}
                                                    sx={{ ml: 1 }}
                                                >
                                                    Dar de baja
                                                </Button>
                                            )}
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                    <Box sx={{ display: "flex", justifyContent: "flex-end", mt: 2 }}>
                        <Button variant="contained" color="primary" onClick={handleGoToAddTool} sx={{ backgroundColor: "rgba(255, 94, 0, 1)" }}>
                            Agregar Herramienta
                        </Button>
                    </Box>
                </Box>
            </Box>
        </>
    );
};

export default ToolList;