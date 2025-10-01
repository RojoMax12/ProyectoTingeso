import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  ThemeProvider, CssBaseline, Typography, Stack, Button,
  Container, Drawer, List, ListItem, ListItemButton, ListItemIcon, ListItemText,
  IconButton, Box, Paper, Card, CardContent, Grid, TextField, Chip,
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Accordion, AccordionSummary, AccordionDetails
} from "@mui/material";
import HomeIcon from "@mui/icons-material/Home";
import BuildIcon from "@mui/icons-material/Build";
import MenuIcon from "@mui/icons-material/Menu";
import LibraryAddIcon from "@mui/icons-material/LibraryAdd";
import AssessmentIcon from "@mui/icons-material/Assessment";
import ContactsIcon from "@mui/icons-material/Contacts";
import PersonAddAltIcon from '@mui/icons-material/PersonAddAlt';
import AdminPanelSettingsIcon from "@mui/icons-material/AdminPanelSettings";
import ReportIcon from '@mui/icons-material/Report';
import BarChartIcon from '@mui/icons-material/BarChart';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';
import WarningIcon from '@mui/icons-material/Warning';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ErrorIcon from '@mui/icons-material/Error';
import StarIcon from '@mui/icons-material/Star';
import FilterListIcon from '@mui/icons-material/FilterList';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { createTheme } from "@mui/material/styles";
import { useKeycloak } from "@react-keycloak/web";
import ReportsService from "../Services/ReportsServices";
import LoanToolsServices from "../Services/LoanToolsServices";
import ToolServices from "../Services/ToolServices";
import ClientServices from "../Services/ClientServices";

const theme = createTheme({
  palette: {
    background: {
      default: "#FEF3E2",
    },
  },
});

const Reports = () => {
  const navigate = useNavigate();
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [activeLoans, setActiveLoans] = useState([]);
  const [clientsWithDelays, setClientsWithDelays] = useState([]);
  const [topTools, setTopTools] = useState([]);
  const [showActiveLoans, setShowActiveLoans] = useState(false);
  const [showClientsWithDelays, setShowClientsWithDelays] = useState(false);
  const [showTopTools, setShowTopTools] = useState(false);
  const { keycloak } = useKeycloak();
  const isAdmin = keycloak?.tokenParsed?.realm_access?.roles?.includes("ADMIN");

  // Datos mock para los reportes que aún no están implementados
  const mockClientsWithDelays = [
    { 
      id: 1, 
      client: "María García", 
      email: "maria@email.com", 
      phone: "+569 1234 5678", 
      toolsOverdue: 1, 
      totalDebt: 15000, 
      daysOverdue: 2 
    },
    { 
      id: 2, 
      client: "Pedro Martínez", 
      email: "pedro@email.com", 
      phone: "+569 8765 4321", 
      toolsOverdue: 2, 
      totalDebt: 28000, 
      daysOverdue: 5 
    }
  ];

  const mockTopTools = [
    { id: 1, name: "Taladro Industrial", category: "Herramientas Eléctricas", timesRented: 45, revenue: 675000, ranking: 1 },
    { id: 2, name: "Sierra Circular", category: "Herramientas de Corte", timesRented: 38, revenue: 570000, ranking: 2 },
    { id: 3, name: "Soldadora", category: "Herramientas de Soldadura", timesRented: 32, revenue: 480000, ranking: 3 },
    { id: 4, name: "Martillo Neumático", category: "Herramientas Neumáticas", timesRented: 28, revenue: 420000, ranking: 4 },
    { id: 5, name: "Lijadora", category: "Herramientas Eléctricas", timesRented: 25, revenue: 375000, ranking: 5 }
  ];

  const sidebarOptions = [
    { text: "Inicio", icon: <HomeIcon />, path: "/" },
    { text: "Herramientas", icon: <BuildIcon />, path: "/ToolList" },
    { text: "Agregar Herramienta", icon: <LibraryAddIcon />, path: "/AddTools" },
    { text: "Ver Kardex", icon: <AssessmentIcon />, path: "/Kardex" },
    { text: "Registrar Cliente", icon: <PersonAddAltIcon />, path: "/RegisterClient" },
    { text: "Clientes", icon: <ContactsIcon />, path: "/ClientList" },
    { text: "Reportes", icon: <ReportIcon />, path: "/Reports" },
    ...(isAdmin ? [{ text: "Configuraciones", icon: <AdminPanelSettingsIcon />, path: "/Configuration" }] : [])
  ];

  // FUNCIÓN PARA OCULTAR TODOS LOS REPORTES
  const hideAllReports = () => {
    setShowActiveLoans(false);
    setShowClientsWithDelays(false);
    setShowTopTools(false);
  };

  // FUNCIONES PARA GENERAR REPORTES (Solo crean)
  const createReportLoan = () => {
    console.log("Generando nuevo reporte de préstamos...");
    
    ReportsService.createLoanReport()
      .then((response) => {
        console.log("Reporte creado exitosamente:", response.data);
        
        if (response.data && response.data.length > 0) {
          alert(`¡${response.data.length} nuevos reportes de préstamos generados exitosamente! Presiona "Mostrar Reportes" para verlos.`);
        } else {
          alert("No se generaron nuevos reportes. Todos los préstamos ya tienen reportes asociados.");
        }
      })
      .catch((error) => {
        console.error("Error al crear el reporte:", error);
        alert("Error al generar el reporte. Inténtalo nuevamente.");
      });
  };

  const createReportClientLate = () => {
    console.log("Generando nuevo reporte de clientes con atrasos...");
    ReportsService.createClientLateReport()
      .then((response) => {
        console.log("Reporte de clientes con atrasos creado exitosamente:", response.data);
        if (response.data && response.data.length > 0) {
          alert(`¡${response.data.length} nuevos reportes de clientes con atrasos generados exitosamente! Presiona "Mostrar Reportes" para verlos.`);
        } else {
          alert("No se generaron nuevos reportes. Todos los clientes con atrasos ya tienen reportes asociados.");
        }
      })
      .catch((error) => {
        console.error("Error al crear el reporte:", error);
        alert("Error al generar el reporte. Inténtalo nuevamente.");
      });
  };

  const generateClientsWithDelaysReport = () => {
    console.log("Generando reporte de clientes con atrasos...");
    alert("Reporte de clientes con atrasos generado exitosamente! Presiona 'Mostrar Reportes' para verlo.");
    // TODO: Implementar servicio real
    // ReportsService.createClientsDelayReport()
  };

  const generateTopToolsReport = () => {
    console.log("Generando ranking de herramientas...");
    alert("Ranking de herramientas generado exitosamente! Presiona 'Mostrar Reportes' para verlo.");
    // TODO: Implementar servicio real
    // ReportsService.createTopToolsReport()
  };

  // FUNCIONES PARA MOSTRAR REPORTES (Solo muestran)
  const showActiveLoansReport = () => {
  console.log("Cargando reportes de préstamos activos...");
  hideAllReports();
  
  ReportsService.getallReportsLoans()
    .then((response) => {
      console.log("✅ Reportes obtenidos de la BD:", response.data);
      
      if (response.data && Array.isArray(response.data) && response.data.length > 0) {
        console.log(`📊 Procesando ${response.data.length} reportes...`);
        
        const loanPromises = response.data.map((report, index) => {
          console.log(`🔍 Procesando reporte ${index + 1}:`, {
            reportId: report.id,
            idLoanTool: report.idLoanTool,
            date: report.date
          });
          
          if (report.idLoanTool) {
            return LoanToolsServices.getid(report.idLoanTool)
              .then((loanRes) => {
                console.log(`✅ Préstamo ${report.idLoanTool} encontrado:`, loanRes.data);
                
                const clientPromise = loanRes.data.clientid ? 
                  ClientServices.getByid(loanRes.data.clientid)
                    .then(res => {
                      console.log(`👤 Cliente ${loanRes.data.clientid}:`, res.data);
                      return res;
                    })
                    .catch(err => {
                      console.error(`❌ Error al buscar cliente ${loanRes.data.clientid}:`, err);
                      return { data: { name: "Cliente no encontrado" } };
                    }) : 
                  Promise.resolve({ data: { name: "Sin cliente asignado" } });
                
                const toolPromise = loanRes.data.toolid ? 
                  ToolServices.getid(loanRes.data.toolid)
                    .then(res => {
                      console.log(`🔧 Herramienta ${loanRes.data.toolid}:`, res.data);
                      return res;
                    })
                    .catch(err => {
                      console.error(`❌ Error al buscar herramienta ${loanRes.data.toolid}:`, err);
                      return { data: { name: "Herramienta no encontrada" } };
                    }) : 
                  Promise.resolve({ data: { name: "Sin herramienta asignada" } });
                
                return Promise.all([clientPromise, toolPromise])
                  .then(([clientRes, toolRes]) => {
                    const today = new Date();
                    const endDate = new Date(loanRes.data.finalreturndate);
                    const isOverdue = today > endDate;
                    const timeDiff = endDate - today;
                    const daysDiff = Math.ceil(timeDiff / (1000 * 60 * 60 * 24));
                    
                    const loanData = {
                      id: loanRes.data.id,
                      reportId: report.id,
                      idLoanTool: report.idLoanTool,
                      client: clientRes.data.name || "Cliente no especificado",
                      tool: toolRes.data.name || "Herramienta no especificada",
                      startDate: loanRes.data.initiallenddate || "No especificada",
                      endDate: loanRes.data.finalreturndate || "No especificada",
                      status: isOverdue ? "Atrasado" : "Vigente",
                      daysLeft: isOverdue ? 0 : daysDiff,
                      daysOverdue: isOverdue ? Math.abs(daysDiff) : 0,
                      rentalFee: loanRes.data.rentalFee || 0,
                      damageFee: loanRes.data.damageFee || 0,
                      reportDate: report.date || "No especificada"
                    };
                    
                    console.log(`✅ Préstamo procesado:`, loanData);
                    return loanData;
                  });
              })
              .catch((error) => {
                console.error(`❌ Error al obtener préstamo ${report.idLoanTool}:`, error);
                return {
                  id: report.id,
                  reportId: report.id,
                  idLoanTool: report.idLoanTool,
                  client: "Error: Préstamo no encontrado",
                  tool: "Error: Datos no disponibles",
                  startDate: report.date || "No especificada",
                  endDate: "No especificada",
                  status: "Error",
                  daysLeft: 0,
                  daysOverdue: 0,
                  rentalFee: 0,
                  damageFee: 0
                };
              });
          } else {
            console.warn(`⚠️ Reporte ${report.id} sin idLoanTool asociado`);
            return Promise.resolve({
              id: report.id,
              reportId: report.id,
              idLoanTool: null,
              client: "Sin préstamo asociado",
              tool: "Sin herramienta",
              startDate: report.date || "No especificada",
              endDate: "No especificada",
              status: "Sin datos",
              daysLeft: 0,
              daysOverdue: 0,
              rentalFee: 0,
              damageFee: 0
            });
          }
        });
        
        Promise.all(loanPromises)
          .then((formattedLoans) => {
            const validLoans = formattedLoans.filter(loan => loan !== null);
            console.log(`📋 Préstamos finales procesados (${validLoans.length}):`, validLoans);
            
            setActiveLoans(validLoans);
            setShowActiveLoans(true);
            
            if (validLoans.length > 0) {
              alert(`✅ Se cargaron ${validLoans.length} reportes de préstamos exitosamente`);
            } else {
              alert("⚠️ No se pudieron procesar los reportes de préstamos");
            }
          })
          .catch((error) => {
            console.error("❌ Error al procesar array de préstamos:", error);
            alert("Error al procesar algunos reportes de préstamos");
          });
      } else {
        console.log("📭 No se encontraron reportes de préstamos");
        alert("No se encontraron reportes de préstamos. Genera reportes primero usando 'Generar Reporte'.");
      }
    })
    .catch((error) => {
      console.error("❌ Error al obtener reportes de préstamos:", error);
      alert("Error al cargar los reportes de préstamos del servidor");
    });
};

const showClientsLateReport = () => {
  console.log("Mostrando reportes de clientes con atrasos...");
  hideAllReports();
  
  ReportsService.getallReportsClientLate()
    .then((response) => {
      console.log("✅ Reportes de clientes con atrasos obtenidos de la BD:", response.data);
      
      if (response.data && Array.isArray(response.data) && response.data.length > 0) {
        console.log(`📊 Procesando ${response.data.length} reportes de clientes con atrasos...`);
        
        const clientPromises = response.data.map((report, index) => {
          console.log(`🔍 Procesando reporte de cliente con atraso ${index + 1}:`, {
            reportId: report.id,
            idClient: report.idClient, // <-- AQUÍ ESTÁ EL ID DEL CLIENTE
            idTool: report.idTool,
            date: report.date
          });
          
          // El reporte ya tiene directamente el idClient
          const clientId = report.idClient;
          
          if (clientId) {
            return ClientServices.getByid(clientId)
              .then((clientRes) => {
                console.log(`👤 Cliente con atraso ${clientId} encontrado:`, clientRes.data);
                
                // Si también tienes idTool, podrías obtener el nombre de la herramienta
                const toolPromise = report.idTool ? 
                  ToolServices.getid(report.idTool)
                    .then(res => {
                      console.log(`🔧 Herramienta ${report.idTool}:`, res.data);
                      return res.data.name || "Herramienta no especificada";
                    })
                    .catch(err => {
                      console.error(`❌ Error al buscar herramienta ${report.idTool}:`, err);
                      return "Herramienta no encontrada";
                    }) : 
                  Promise.resolve("Sin herramienta especificada");
                
                return toolPromise.then((toolName) => {
                  const clientData = {
                    id: clientRes.data.id || report.id,
                    reportId: report.id,
                    client: clientRes.data.name || "Cliente no especificado",
                    email: clientRes.data.email || "Email no disponible",
                    phone: clientRes.data.phone || "Teléfono no disponible",
                    toolName: toolName,
                    daysOverdue: report.daysOverdue || 0,
                    totalDebt: report.totalDebt || 0,
                    endDate: report.endDate || report.date || "No especificada",
                    toolsOverdue: 1,
                    reportDate: report.date || "No especificada"
                  };
                  
                  console.log(`✅ Cliente con atraso procesado:`, clientData);
                  return clientData;
                });
              })
              .catch((error) => {
                console.error(`❌ Error al buscar cliente ${clientId}:`, error);
                return {
                  id: report.id,
                  reportId: report.id,
                  client: "Error: Cliente no encontrado",
                  email: "Error",
                  phone: "Error",
                  toolName: "Error: Datos no disponibles",
                  daysOverdue: 0,
                  totalDebt: 0,
                  endDate: "Error",
                  toolsOverdue: 0
                };
              });
          } else {
            console.warn(`⚠️ Reporte ${report.id} sin idClient asociado`);
            return Promise.resolve({
              id: report.id,
              reportId: report.id,
              client: "Sin cliente asociado",
              email: "Sin email",
              phone: "Sin teléfono",
              toolName: "Sin herramienta",
              daysOverdue: 0,
              totalDebt: 0,
              endDate: "Sin fecha",
              toolsOverdue: 0
            });
          }
        });
        
        Promise.all(clientPromises)
          .then((formattedClients) => {
            const validClients = formattedClients.filter(client => client !== null);
            console.log(`📋 Clientes con atrasos finales procesados (${validClients.length}):`, validClients);
            
            setClientsWithDelays(validClients);
            setShowClientsWithDelays(true);
            
            if (validClients.length > 0) {
              alert(`✅ Se cargaron ${validClients.length} reportes de clientes con atrasos exitosamente`);
            } else {
              alert("⚠️ No se encontraron clientes con atrasos");
            }
          })
          .catch((error) => {
            console.error("❌ Error al procesar array de clientes con atrasos:", error);
            alert("Error al procesar algunos reportes de clientes con atrasos");
          });
      } else {
        console.log("📭 No se encontraron reportes de clientes con atrasos");
        alert("No se encontraron reportes de clientes con atrasos. Genera reportes primero usando 'Generar Reporte'.");
      }
    })
    .catch((error) => {
      console.error("❌ Error al obtener reportes de clientes con atrasos:", error);
      alert("Error al cargar los reportes de clientes con atrasos del servidor");
    });
};


  const showTopToolsReport = () => {
    console.log("Mostrando ranking de herramientas más prestadas...");
    hideAllReports();
    setTopTools(mockTopTools);
    setShowTopTools(true);
  };

  // FUNCIÓN PARA FILTRO POR FECHAS
  const getreportByDate = () => {
    if (!startDate || !endDate) {
      alert("Por favor, selecciona ambas fechas para filtrar.");
      return;
    }
    
    console.log("Filtrando reportes desde:", startDate, "hasta:", endDate);
    
    ReportsService.reportdate(startDate, endDate)
      .then((response) => {
        console.log("Reportes filtrados por fecha:", response.data);
        
        if (response.data && Array.isArray(response.data) && response.data.length > 0) {
          alert(`Se encontraron ${response.data.length} registros en el rango de fechas seleccionado.`);
          // TODO: Mostrar los resultados filtrados en una tabla específica
        } else {
          alert("No se encontraron registros en el rango de fechas seleccionado.");
        }
      })
      .catch((error) => {
        console.error("Error al obtener el reporte:", error);
        alert("Error al filtrar reportes por fecha. Verifica tu conexión.");
      });
  };

  // CONFIGURACIÓN DE TIPOS DE REPORTES
  const reportTypes = [
    {
      title: "Préstamos Activos",
      description: "Lista todos los préstamos vigentes y atrasados con su estado actual",
      icon: <BarChartIcon sx={{ fontSize: 40, color: "rgba(255, 94, 0, 1)" }} />,
      generateAction: createReportLoan,
      showAction: showActiveLoansReport
    },
    {
      title: "Clientes con Atrasos",
      description: "Información detallada de clientes que tienen préstamos vencidos",
      icon: <WarningIcon sx={{ fontSize: 40, color: "rgba(255, 94, 0, 1)" }} />,
      generateAction: createReportClientLate,
      showAction: showClientsLateReport
    },
    {
      title: "Herramientas Más Prestadas",
      description: "Ranking de las herramientas más solicitadas y rentables",
      icon: <StarIcon sx={{ fontSize: 40, color: "rgba(255, 94, 0, 1)" }} />,
      generateAction: generateTopToolsReport,
      showAction: showTopToolsReport
    }
  ];

  // FUNCIONES AUXILIARES
  const getStatusColor = (status) => {
    switch (status) {
      case "Vigente":
        return { color: "#2e7d32", bgColor: "#e8f5e8" };
      case "Atrasado":
        return { color: "#d32f2f", bgColor: "#ffebee" };
      default:
        return { color: "#757575", bgColor: "#f5f5f5" };
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('es-CL', {
      style: 'currency',
      currency: 'CLP'
    }).format(amount);
  };

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
    
      <IconButton
                color="inherit"
                onClick={() => setDrawerOpen(true)}
                sx={{ position: "fixed", top: 16, left: 16, zIndex: 10, backgroundColor: "#FA812F", boxShadow: 3 , '&:hover': { backgroundColor: "#FA812F" }}}
            >
                <MenuIcon sx={{ color: "#FEF3E2" }} />
            </IconButton>

      {/* SIDEBAR */}
      <Drawer
        open={drawerOpen}
        onClose={() => setDrawerOpen(false)}
        variant="temporary"
        sx={{
          [`& .MuiDrawer-paper`]: { 
            width: 240, 
            boxSizing: "border-box", 
            backgroundColor: "#FEF3E2" 
          }
        }}
      >
        <List>
          {sidebarOptions.map((option) => (
            <ListItem key={option.text} disablePadding>
              <ListItemButton 
                onClick={() => { navigate(option.path); setDrawerOpen(false); }}
                sx={{
                  '&:hover': {
                    backgroundColor: 'rgba(255, 94, 0, 0.1)',
                  },
                  borderRadius: 1,
                  mx: 1,
                  my: 0.5
                }}
              >
                <ListItemIcon sx={{ color: "rgba(255, 94, 0, 1)", minWidth: 40 }}>
                  {option.icon}
                </ListItemIcon>
                <ListItemText 
                  primary={option.text} 
                  sx={{
                    '& .MuiListItemText-primary': {
                      fontWeight: 500,
                      fontSize: '0.95rem'
                    }
                  }}
                />
              </ListItemButton>
            </ListItem>
          ))}
        </List>
      </Drawer>

      {/* CONTENIDO PRINCIPAL */}
      <Container sx={{ mt: 8, py: 4, minHeight: "100vh" }}>
        <Box sx={{ textAlign: "center", mb: 6 }}>
          <Typography 
            variant="h3" 
            sx={{ 
              fontWeight: "bold", 
              color: "rgba(255, 94, 0, 1)", 
              mb: 2 
            }}
          >
            📊 Centro de Reportes
          </Typography>
          <Typography 
            variant="h6" 
            sx={{ 
              color: "text.secondary", 
              fontStyle: "italic",
              maxWidth: 600,
              mx: "auto"
            }}
          >
            Genera reportes detallados y análisis de tu negocio de alquiler de herramientas
          </Typography>
        </Box>

        {/* FILTROS DE FECHA */}
        <Paper
          sx={{
            p: 3,
            mb: 4,
            borderRadius: 4,
            backgroundColor: "#fff8f0",
            border: "2px solid rgba(255, 94, 0, 0.1)"
          }}
        >
          <Typography 
            variant="h6" 
            sx={{ 
              fontWeight: "bold", 
              mb: 3, 
              color: "rgba(255, 94, 0, 1)",
              display: "flex",
              alignItems: "center",
              gap: 1
            }}
          >
            <FilterListIcon /> Filtros de Fecha
          </Typography>
          <Stack direction={{ xs: "column", sm: "row" }} spacing={2} alignItems="center">
            <TextField
              label="Fecha Inicio"
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              InputLabelProps={{ shrink: true }}
              sx={{ 
                minWidth: 200,
                '& input[type="date"]::-webkit-calendar-picker-indicator': {
                  filter: 'brightness(0) saturate(100%) invert(53%) sepia(94%) saturate(5783%) hue-rotate(14deg) brightness(102%) contrast(101%)',
                  cursor: 'pointer'
                }
              }}
            />
            <TextField
              label="Fecha Fin"
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              InputLabelProps={{ shrink: true }}
              sx={{ 
                minWidth: 200,
                '& input[type="date"]::-webkit-calendar-picker-indicator': {
                  filter: 'brightness(0) saturate(100%) invert(53%) sepia(94%) saturate(5783%) hue-rotate(14deg) brightness(102%) contrast(101%)',
                  cursor: 'pointer'
                }
              }}
            />
            <Button
              variant="contained"
              startIcon={<CalendarTodayIcon sx={{ color: "white" }} />}
              onClick={getreportByDate}
              sx={{
                backgroundColor: "rgba(255, 94, 0, 1)",
                borderRadius: 3,
                px: 3,
                py: 1.5,
                '&:hover': {
                  backgroundColor: "rgba(255, 94, 0, 0.8)"
                }
              }}
            >
              Aplicar Filtro
            </Button>
          </Stack>
        </Paper>

        {/* GRID DE REPORTES */}
        <Grid container spacing={4} sx={{ mb: 4 }}>
          {reportTypes.map((report, index) => (
            <Grid item xs={12} md={4} key={index}>
              <Card
                sx={{
                  height: "100%",
                  borderRadius: 4,
                  boxShadow: "0 8px 32px rgba(255, 94, 0, 0.1)",
                  border: "2px solid rgba(255, 94, 0, 0.1)",
                  transition: "all 0.3s ease",
                  '&:hover': {
                    boxShadow: "0 12px 48px rgba(255, 94, 0, 0.2)",
                    transform: "translateY(-8px)",
                    borderColor: "rgba(255, 94, 0, 0.3)"
                  }
                }}
              >
                <CardContent sx={{ p: 3, textAlign: "center" }}>
                  <Box sx={{ mb: 2 }}>
                    {report.icon}
                  </Box>
                  <Typography 
                    variant="h6" 
                    sx={{ 
                      fontWeight: "bold", 
                      mb: 1, 
                      color: "rgba(255, 94, 0, 1)" 
                    }}
                  >
                    {report.title}
                  </Typography>
                  <Typography 
                    variant="body2" 
                    sx={{ 
                      color: "text.secondary", 
                      mb: 3,
                      minHeight: 40
                    }}
                  >
                    {report.description}
                  </Typography>
                  
                  {/* BOTÓN GENERAR REPORTE */}
                  <Button
                    variant="contained"
                    fullWidth
                    onClick={report.generateAction}
                    sx={{
                      backgroundColor: "rgba(255, 94, 0, 1)",
                      borderRadius: 3,
                      py: 1.5,
                      mb: 1,
                      '&:hover': {
                        backgroundColor: "rgba(255, 94, 0, 0.8)"
                      }
                    }}
                  >
                    🔄 Generar Reporte
                  </Button>

                  {/* BOTÓN MOSTRAR REPORTES */}
                  <Button
                    variant="outlined"
                    fullWidth
                    onClick={report.showAction}
                    sx={{
                      borderColor: "rgba(255, 94, 0, 1)",
                      color: "rgba(255, 94, 0, 1)",
                      borderRadius: 3,
                      py: 1.5,
                      '&:hover': {
                        backgroundColor: "rgba(255, 94, 0, 0.1)",
                        borderColor: "rgba(255, 94, 0, 0.8)"
                      }
                    }}
                  >
                    👁️ Mostrar Reportes
                  </Button>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>

        {/* REPORTE DE PRÉSTAMOS ACTIVOS */}
        {showActiveLoans && (
          <Accordion
            expanded={true}
            sx={{
              mb: 3,
              borderRadius: 4,
              boxShadow: "0 4px 20px rgba(255, 94, 0, 0.1)",
              '&:before': { display: 'none' }
            }}
          >
            <AccordionSummary
              expandIcon={<ExpandMoreIcon />}
              sx={{
                backgroundColor: "rgba(255, 94, 0, 0.05)",
                borderRadius: 4
              }}
            >
              <Typography variant="h6" sx={{ fontWeight: "bold", color: "rgba(255, 94, 0, 1)" }}>
                📋 Préstamos Activos ({activeLoans.length} registros)
              </Typography>
            </AccordionSummary>
            <AccordionDetails>
              <TableContainer>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell sx={{ fontWeight: "bold" }}>Fecha Reporte</TableCell>
                      <TableCell sx={{ fontWeight: "bold" }}>Cliente</TableCell>
                      <TableCell sx={{ fontWeight: "bold" }}>Herramienta</TableCell>
                      <TableCell sx={{ fontWeight: "bold" }}>Fecha Inicio</TableCell>
                      <TableCell sx={{ fontWeight: "bold" }}>Fecha Fin</TableCell>
                      <TableCell sx={{ fontWeight: "bold" }}>Estado</TableCell>
                      <TableCell sx={{ fontWeight: "bold" }}>Días</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {activeLoans.map((loan) => {
                      const statusStyle = getStatusColor(loan.status);
                      return (
                        <TableRow key={loan.id}>
                          <TableCell>{loan.reportDate}</TableCell>
                          <TableCell>{loan.client}</TableCell>
                          <TableCell>{loan.tool}</TableCell>
                          <TableCell>{loan.startDate}</TableCell>
                          <TableCell>{loan.endDate}</TableCell>
                          <TableCell>
                            <Chip
                              label={loan.status}
                              size="small"
                              icon={loan.status === "Vigente" ? <CheckCircleIcon /> : <ErrorIcon />}
                              sx={{
                                backgroundColor: statusStyle.bgColor,
                                color: statusStyle.color,
                                fontWeight: "bold"
                              }}
                            />
                          </TableCell>
                          <TableCell>
                            {loan.status === "Vigente" ? (
                              <Typography color="success.main">
                                {loan.daysLeft} días restantes
                              </Typography>
                            ) : (
                              <Typography color="error.main">
                                {loan.daysOverdue} días de atraso
                              </Typography>
                            )}
                          </TableCell>
                        </TableRow>
                      );
                    })}
                  </TableBody>
                </Table>
              </TableContainer>
            </AccordionDetails>
          </Accordion>
        )}

        {/* REPORTE DE CLIENTES CON ATRASOS */}
        {showClientsWithDelays && (
          <Accordion
            expanded={true}
            sx={{
              mb: 3,
              borderRadius: 4,
              boxShadow: "0 4px 20px rgba(255, 94, 0, 0.1)",
              '&:before': { display: 'none' }
            }}
          >
            <AccordionSummary
              expandIcon={<ExpandMoreIcon />}
              sx={{
                backgroundColor: "rgba(255, 94, 0, 0.05)",
                borderRadius: 4
              }}
            >
              <Typography variant="h6" sx={{ fontWeight: "bold", color: "rgba(255, 94, 0, 1)" }}>
                ⚠️ Clientes con Atrasos ({clientsWithDelays.length} registros)
              </Typography>
            </AccordionSummary>
            <AccordionDetails>
              <TableContainer>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell sx={{ fontWeight: "bold" }}>Fecha Reporte</TableCell>
                      <TableCell sx={{ fontWeight: "bold" }}>Cliente</TableCell>
                      <TableCell sx={{ fontWeight: "bold" }}>Email</TableCell>
                      <TableCell sx={{ fontWeight: "bold" }}>Teléfono</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {clientsWithDelays.map((client) => (
                      <TableRow key={client.id}>
                        <TableCell>{client.reportDate}</TableCell>
                        <TableCell sx={{ fontWeight: "bold" }}>{client.client}</TableCell>
                        <TableCell>{client.email}</TableCell>
                        <TableCell>{client.phone}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </AccordionDetails>
          </Accordion>
        )}

        {/* REPORTE DE HERRAMIENTAS MÁS PRESTADAS */}
        {showTopTools && (
          <Accordion
            expanded={true}
            sx={{
              mb: 3,
              borderRadius: 4,
              boxShadow: "0 4px 20px rgba(255, 94, 0, 0.1)",
              '&:before': { display: 'none' }
            }}
          >
            <AccordionSummary
              expandIcon={<ExpandMoreIcon />}
              sx={{
                backgroundColor: "rgba(255, 94, 0, 0.05)",
                borderRadius: 4
              }}
            >
              <Typography variant="h6" sx={{ fontWeight: "bold", color: "rgba(255, 94, 0, 1)" }}>
                🏆 Herramientas Más Prestadas ({topTools.length} registros)
              </Typography>
            </AccordionSummary>
            <AccordionDetails>
              <TableContainer>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell sx={{ fontWeight: "bold" }}>Ranking</TableCell>
                      <TableCell sx={{ fontWeight: "bold" }}>Herramienta</TableCell>
                      <TableCell sx={{ fontWeight: "bold" }}>Categoría</TableCell>
                      <TableCell sx={{ fontWeight: "bold" }}>Veces Prestada</TableCell>
                      <TableCell sx={{ fontWeight: "bold" }}>Ingresos Generados</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {topTools.map((tool) => (
                      <TableRow key={tool.id}>
                        <TableCell>
                          <Stack direction="row" alignItems="center" spacing={1}>
                            <Chip
                              label={`#${tool.ranking}`}
                              size="small"
                              sx={{
                                backgroundColor: tool.ranking === 1 ? "#FFD700" : 
                                                tool.ranking === 2 ? "#C0C0C0" : 
                                                tool.ranking === 3 ? "#CD7F32" : "rgba(255, 94, 0, 0.1)",
                                color: tool.ranking <= 3 ? "#000" : "rgba(255, 94, 0, 1)",
                                fontWeight: "bold"
                              }}
                            />
                            {tool.ranking <= 3 && <StarIcon sx={{ color: "#FFD700", fontSize: 20 }} />}
                          </Stack>
                        </TableCell>
                        <TableCell sx={{ fontWeight: "bold" }}>{tool.name}</TableCell>
                        <TableCell>{tool.category}</TableCell>
                        <TableCell>
                          <Chip
                            label={tool.timesRented}
                            size="small"
                            color="primary"
                            sx={{ fontWeight: "bold" }}
                          />
                        </TableCell>
                        <TableCell sx={{ fontWeight: "bold", color: "success.main" }}>
                          {formatCurrency(tool.revenue)}
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </AccordionDetails>
          </Accordion>
        )}

        {/* FOOTER INFORMATIVO */}
        <Box sx={{ textAlign: "center", mt: 6, py: 3 }}>
          <Typography 
            variant="body2" 
            sx={{ 
              color: "text.secondary",
              fontStyle: "italic"
            }}
          >
            💡 <strong>Tip:</strong> Usa "Generar Reporte" para crear nuevos datos y "Mostrar Reportes" para ver los existentes
          </Typography>
        </Box>

      </Container>
    </ThemeProvider>
  );
};

export default Reports;