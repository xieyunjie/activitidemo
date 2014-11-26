package DemoTest;

import java.io.FileInputStream;
import java.util.List;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

public class DemoProcessTest {

	private static String realPath = "C:\\Users\\yunjie\\Desktop\\workspace\\ativitidemo\\src\\main\\resources\\diagrams";

	public static void main(String[] args) throws Exception {
		// ���� Activiti��������
		// ��ʽһ �Զ�Ѱ��activiti.cfg.xml
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

		// ��ʽ�� ָ������activiti.cfg.xml
		// ProcessEngine processEngine = ProcessEngineConfiguration
		// .createProcessEngineConfigurationFromResource("activiti.cfg.xml")
		// .buildProcessEngine();

		// ȡ�� Activiti ����
		RepositoryService repositoryService = processEngine
				.getRepositoryService();
		RuntimeService runtimeService = processEngine.getRuntimeService();

		// �������̶���
		repositoryService
				.createDeployment()
				.addInputStream("DemoProcess.bpmn",
						new FileInputStream(realPath + "\\DemoProcess.bpmn"))
				.addInputStream("DemoProcess.png",
						new FileInputStream(realPath + "\\DemoProcess.png"))
				.deploy();

		// ��������ʵ��
		ProcessInstance instance = processEngine.getRuntimeService()
				.startProcessInstanceByKey("DemoProcess");
		String procId = instance.getId();
		System.out.println("procId:" + procId);

		// ��õ�һ������
		TaskService taskService = processEngine.getTaskService();
		List<Task> tasks = taskService.createTaskQuery()
				.taskDefinitionKey("firstTask").list();
		for (Task task : tasks) {
			System.out.println("Following task is: taskID -" + task.getId()
					+ " taskName -" + task.getName());
			// ��������
			taskService.claim(task.getId(), "testUser");
		}
		
		System.out.println("Number of tasks for testUser: "
				+ taskService.createTaskQuery().taskAssignee("testUser")
						.count());

		// �鿴testUser �����Ƿ��ܹ���ȡ��������
		tasks = taskService.createTaskQuery().taskAssignee("testUser").list();
		for (Task task : tasks) {
			System.out.println("Task for testUser: " + task.getName());
			// �������
			taskService.complete(task.getId());
		}
		System.out.println("Number of tasks for testUser: "
				+ taskService.createTaskQuery().taskAssignee("testUser")
						.count());

		// ��ȡ������ڶ�������
		tasks = taskService.createTaskQuery().taskDefinitionKey("secondTask")
				.list();
		for (Task task : tasks) {
			System.out.println("Following task is : taskID -" + task.getId()
					+ " taskName -" + task.getName());
			taskService.claim(task.getId(), "testUser");
		}

		// ��ɵڶ������������������
		for (Task task : tasks) {
			taskService.complete(task.getId());
		}

		// ��ʵ�����Ƿ����
		HistoryService historyService = processEngine.getHistoryService();
		HistoricProcessInstance historicProcessInstance = historyService
				.createHistoricProcessInstanceQuery().processInstanceId(procId)
				.singleResult();
		System.out.println("Process instance end time: "
				+ historicProcessInstance.getEndTime());
	}
}
