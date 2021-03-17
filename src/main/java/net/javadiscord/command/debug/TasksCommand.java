package net.javadiscord.command.debug;

import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.RequiredArgsConstructor;
import net.javadiscord.command.Command;
import net.javadiscord.util.Messages;
import org.reactivestreams.Publisher;
import org.springframework.scheduling.config.*;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Component;

import java.time.Clock;

/**
 * Lists the currently scheduled tasks that this bot will perform.
 */
@Component
@RequiredArgsConstructor
public class TasksCommand implements Command {
	private final ScheduledTaskHolder taskHolder;

	@Override
	public Publisher<?> handle(MessageCreateEvent event, String[] args) {
		return Messages.respondWithEmbed(event, spec -> {
			spec.setTitle("Scheduled Tasks");
			for (ScheduledTask task : this.taskHolder.getScheduledTasks()) {
				Task t = task.getTask();
				String tName = "";
				String tDescription = "";
				if (t instanceof CronTask) {
					CronTask ct = (CronTask) t;
					tDescription = String.format(
							"_CronTask_: `%s`,\n_Next execution_: `%s`",
							ct.getExpression(),
							ct.getTrigger().nextExecutionTime(new SimpleTriggerContext(Clock.systemUTC()))
					);
				} else if (t instanceof IntervalTask) {
					IntervalTask it = (IntervalTask) t;
					tDescription = "IntervalTask: Initial delay = " + it.getInitialDelay() + "ms, Interval = " + it.getInterval();
				}

				if (t.getRunnable() instanceof ScheduledMethodRunnable) {
					ScheduledMethodRunnable smr = (ScheduledMethodRunnable) t.getRunnable();
					tName = smr.getMethod().getDeclaringClass().getSimpleName() + "::" + smr.getMethod().getName();
				}

				spec.addField(tName, tDescription, true);
			}
		});
	}
}
