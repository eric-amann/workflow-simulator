# Workflow Simulator

This tool is an agent-based workflow simulator for the simulation of BPMN processes. The simulation works by building an environment in which agents work on a process according to their configuration. Agents have their own predefined behavior and search for, claim and complete process tasks correspondingly. Simulations can be enhanced by including context data, which can be configured to influence many of the simulation parameters. The simulator includes the following key features:

*   *Create Realistic Process Executions* - Processes are simulated through the execution in a workflow management system. The event log is a process of the interaction of simulated agents with their environment.
*   *Context Data Integration* - Context data can be integrated in the simulation to build realistic scenarios. With context data any aspect of the simulation can be modified.
*   *Vast Configurability* - The tool already provides a large number of configuration properties for users to build their simulations.


# Usage

## Prerequisites
### 1 Camunda Setup

The simulator works by having the agents interact with a workflow management system. We use Camunda 8 for this purpose. Therefore, it is necessary to have access to a running instance of Camunda 8 for the simulator to interact with. Because the tool requires access to the Camunda 8 database, the self-hosted version is required and the SaaS version will not work. To get your Camunda 8 instance running you can either refer to the [official setup guide](https://docs.camunda.io/docs/self-managed/setup/overview/) or use the [Docker image](https://github.com/camunda/camunda-platform) provided in the *example_scenario* folder. Start the Docker image by running the following command from the *example_scenario* folder.

```
docker compose -f docker-compose.yaml up
```

Make sure that all required components are running correctly by accessing them in your brower:

(The default login credentials are: *username:* demo, *password:* demo)

* **Operate** - default: http://localhost:8081
* **Tasklist** - default :http://localhost:8082
* **Identity** - default: http://localhost:8084

### 2 Application Registration

If all components are up and running, the last step is to register the simulator with the Camunda Identity module, so that requests from the simulator to Camunda are authenticated. To do so open the Camunda Identity service (default http://localhost:8084). Click on *Add application* and enter a descriptive name (e.g. Workflow-Simulator). Enter any value to Redirect URIs (e.g. http://localhost). Make sure that your application has acces to the required APIs. Click on your new appliction in the list and then on *Access to APIs*. Add the following permissions to the application by clicking on *Assign permissions*:
* **Operate API**:  read/write
* **Tasklist API**: read/write
* **Zeebe API**:    write

## Starting the Simulator
### 1 Java Setup

The simulator comes as an executable java program. The simulator requires **version 21** of the Java Runtime Engine. Check if and what version of Java you are running by runnign the following command:
```
java -version
```
### 2 Execution
Once you have cloned the repository you can run the code in your IDE as a maven project, or use the pre-build executable .jar file provided in the example folder.

__Run it in your IDE (Spring-Boot)__
1.  Download or clone the repository and open the *code* folder in your IDE
2.  Locate the main class *WorkflowSimulatorApplication.java*
3.  Run the project

__Run the .jar file__

You can also use the pre-build .jar file. To do so navigate to the *example* folder and run the following command:
```
java -jar workflow-simulator.jar
```
### 3 Configuration
Once executed you should see the program starting. From there interations are possible through console commands. The last step is to enter the client ID and the client secret obtained from the [second step of the Camunda Setup](#2-application-registration). Enter the following command to save your client ID:
```
clientId "{your-client-id}"
```
Also save the client secret:
```
clientSecret {your-client-secret}
```

If your Camunda 8 instance is not avaialbe at the default location (e.g. Operate at localhost:8081) you can create a setup configuration YAML file and load it using the the following command:
```
setup {path-to-your-setup-file}
```
You can find an example setup configuration file with explanations for all availabe configuration values in the *examples* folder.

## Configuring your Simulation
### 1 Configuration Setup
Simulations are configured in a YAML file. Many values can be set to help you create the simulation environment you want. Please refert to the *simulation-config-explanation.yaml* file in this repository for a complete overview and description of all possible configuration attribuates. Note, that for many attributes, a list of values can be given.

If you want to quickly try out the simulator, you can test it using the *example-configuration.yaml* file in the *example* folder. Make sure to change the paths for the BPMN anc context files in the configuration to the ones in your system.

### 2 Load Configuration
Once the configuration and the simulator are ready, you can load the configuration file with the following command:
```
load-config "{path-to-your-configuration-file}"
```
You should see the following output of the configuration was correctly loaded:
```

        ---------------------------------------
                        MaDKit
                   version: 5.3.2
                build-id: 20211130-1017
               MaDKit Team (c) 1997-2024
        ---------------------------------------

Successfully loaded simulation configuration file example-configuration.yaml
```
### 3 Start Simulation
Now, everything is setup for the execution of the simulation. Start it by entering the following command:
```
start "{simulation-id}"
```
The simulation ID is specified in the configuration file. For the example scenario this would be *restaurant_example*
```
start "restaurant_example"
```