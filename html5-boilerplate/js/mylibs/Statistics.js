function Statistics() {
			var self = {};
			self.columnChartId = 'stats-graph-column';
			self.lineChartId = 'stats-graph-line';
			self.width = 270;
			self.height = 180;
			self.lineChart = undefined;
			self.columnChart = undefined;
			self.displayStats = function(stats) {
				var googChart;
				var i;
				if (!stats || !stats.values) {
					return;
				}
				var chartType = stats.chartType || 'line';
				var label = stats.label ? $.trim(stats.label) : '';
				var numPrefix = stats.prefix || '';
				var numSuffix = stats.suffix || '';
				var labels = stats.labels;
				var values = stats.values;
				var chartOptions = {
					legend : 'none',
					width : self.width,
					height : self.height,
					title : label,
					titleTextStyle : {
						color : 'green',
						fontSize : '16'
					}
				};
				var formatOptions = {
					prefix : numPrefix,
					suffix : numSuffix,
					fractionDigits : 0,
					negativeColor : 'red',
					negativeParens : true
				};
				var numFormatter = new google.visualization.NumberFormat(
						formatOptions);
				var chartData = new google.visualization.DataTable();
				if (chartType === 'column') {
					if (!self.columnChart) {
						self.columnChart = new google.visualization.ColumnChart(
								document.getElementById(self.columnChartId));
						google.visualization.events
								.addListener(self.columnChart, 'ready',
										self.showColumnChart);
					}
					googChart = self.columnChart;
				} else { // assume line
					if (!self.lineChart) {
						self.lineChart = new google.visualization.LineChart(
								document.getElementById(self.lineChartId));
						google.visualization.events.addListener(self.lineChart,
								'ready', self.showLineChart);
					}
					googChart = self.lineChart;
				}
				chartData.addColumn('string', stats.xaxis);
				chartData.addColumn('number', stats.yaxis);
				chartData.addRows(values.length);
				for (i = 0; i < values.length; i++) {
					if (labels && labels[i] !== undefined) {
						chartData.setValue(i, 0, labels[i]);
					}
					chartData.setValue(i, 1, values[i]);
				}
				numFormatter.format(chartData, 1);
				if (stats.xaxis) {
					chartOptions.hAxis = {
						title : stats.xaxis
					};
				}
				if (stats.yaxis) {
					chartOptions.vAxis = {
						title : stats.yaxis
					};
				}
				googChart.draw(chartData, chartOptions);
			};
			self.showColumnChart = function() {
				$('#' + self.lineChartId).hide();
				$('#statsbox,#' + self.columnChartId).show();
			};
			self.showLineChart = function() {
				$('#' + self.columnChartId).hide();
				$('#statsbox,#' + self.lineChartId).show();
			};
			self.hideCharts = function() {
				$('#statsbox').hide();
			};
			return self;
}
