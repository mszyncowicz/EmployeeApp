package org.company.controller;

import org.company.dto.EmployeeDTO;
import org.company.dto.GeneralResponseDTO;
import org.company.dto.RegisteredEmployeeDTO;
import org.company.service.EmployeeService;
import org.company.util.SpecificationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/Employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }

    @PostMapping(value = "/insert", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity insertEmployee(@RequestBody EmployeeDTO employeeDTO)
    {
        RegisteredEmployeeDTO registeredEmployeeDTO = employeeService.insertEmployee(employeeDTO).orElse(null);
        if (registeredEmployeeDTO != null)
        {
            return new ResponseEntity<>(registeredEmployeeDTO, new HttpHeaders(), HttpStatus.CREATED);
        }
        else {
            return new ResponseEntity<>(GeneralResponseDTO.SERVER_ERROR, new HttpHeaders(), GeneralResponseDTO.SERVER_ERROR.getStatus());
        }
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GeneralResponseDTO> updateEmployee(@PathVariable Integer id, @RequestBody EmployeeDTO employeeDTO)
    {
        GeneralResponseDTO generalResponseDTO = employeeService.updateEmployeOfIdWithData(employeeDTO, id);
        return new ResponseEntity<>(generalResponseDTO, new HttpHeaders(), generalResponseDTO.getStatus());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<GeneralResponseDTO> deleteEmployee(@PathVariable Integer id)
    {
        GeneralResponseDTO generalResponseDTO = employeeService.deleteEmployeeById(id);
        return new ResponseEntity<>(generalResponseDTO,new HttpHeaders(), generalResponseDTO.getStatus());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity findEmployeeById(@PathVariable("id") Integer id)
    {
        RegisteredEmployeeDTO byId = employeeService.findById(id).orElse(null);
        if (byId != null){
            return new ResponseEntity<>(byId,new HttpHeaders(),HttpStatus.OK);
        } else {
            return new ResponseEntity<>(GeneralResponseDTO.EMPLOYEE_DOES_NOT_EXISTS, new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/find", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<RegisteredEmployeeDTO> findEmployeeBy(@RequestBody EmployeeDTO employeeDTO)
    {
        return employeeService.findBy(employeeDTO);
    }

    @GetMapping(value = "/find", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<RegisteredEmployeeDTO> findEmployeeByGet(ServletRequest request)
    {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<EmployeeField, String[]> filteredMap = new HashMap<>();
        if (parameterMap.containsKey("name"))
            filteredMap.put(EmployeeField.NAME,parameterMap.get("name"));
        if (parameterMap.containsKey("grade"))
            filteredMap.put(EmployeeField.GRADE,parameterMap.get("grade"));
        if (parameterMap.containsKey("surname"))
            filteredMap.put(EmployeeField.SURNAME,parameterMap.get("surname"));
        if (parameterMap.containsKey("salary"))
            filteredMap.put(EmployeeField.SALARY,parameterMap.get("salary"));

        return employeeService.findBySpecification(SpecificationUtils.getSpecificationFromParameters(filteredMap));
    }
}
