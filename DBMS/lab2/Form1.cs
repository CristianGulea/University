using System;
using System.Data;
using System.Data.SqlClient;
using System.Windows.Forms;
using System.Configuration;
using System.Collections.Generic;

namespace SGBDL1
{
    public partial class Form1 : Form
    {      
        static string link = ConfigurationManager.ConnectionStrings["cs"].ConnectionString;
        SqlConnection conn = new SqlConnection(link);
        DataSet ds = new DataSet();
        SqlDataAdapter adapter = new SqlDataAdapter();
        private int ID;

        [Obsolete]
        public Form1()
        {
            InitializeComponent();
            Generate_text_box();
            label1.Text = ConfigurationManager.AppSettings["table_name_parent"];
            label2.Text = ConfigurationManager.AppSettings["table_name_child"];

        }

        [Obsolete]
        private void Generate_text_box()
        {
            try
            {
                List<string> columnsName = new List<string>(ConfigurationSettings.AppSettings["columns_name_child"].Split(','));
                int pointX = 80;
                int pointY = 40;
                int numberOfColumnd = Convert.ToInt32(ConfigurationManager.AppSettings["number_of_columns_child"]);
                panel1.Controls.Clear();
                foreach (string columnName in columnsName)
                {
                    Label l = new Label();
                    l.Text = columnName;
                    TextBox a = new TextBox();
                    a.Name = columnName;
                    a.Location = new System.Drawing.Point(pointX, pointY);
                    l.Location = new System.Drawing.Point(pointX, pointY - 14);
                    a.Visible = true;
                    a.Parent = panel1;
                    l.Parent = panel1;
                    panel1.Show();
                    pointY += 60;
                }
            }
            catch (Exception)
            {
                MessageBox.Show("Error from generate TextBox!");
            }
            

        }

        [Obsolete]
        private void AfisareMagazine_Click(object sender, EventArgs e)
        {
            string select = ConfigurationSettings.AppSettings["select_parent"];
            adapter.SelectCommand = new SqlCommand(select, conn);
            ds.Clear();
            adapter.Fill(ds);
            dataGridView1.DataSource = ds.Tables[0];
        }

        [Obsolete]
        private void AfisareManageri_Click(object sender, EventArgs e)
        {
            string select = ConfigurationSettings.AppSettings["select_child"];
            adapter.SelectCommand = new SqlCommand(select, conn);
            DataTable dataTable = new DataTable();
            adapter.Fill(dataTable);
            dataGridView2.DataSource = dataTable;
        }

        [Obsolete]
        private void ButonAdaugare_Click(object sender, EventArgs e)
        {
            try
            {
                string command = "insert into " + ConfigurationSettings.AppSettings["table_name_child"] + "(" + ConfigurationSettings.AppSettings["columns_name_child"] + ") values " + "(" + ConfigurationSettings.AppSettings["columns_name_child_insert_parameter"] + ")";
                adapter.InsertCommand = new SqlCommand(command, conn);
                List<string> columnsNameList = new List<string>(ConfigurationSettings.AppSettings["columns_name_child"].Split(','));

                foreach (string columnName in columnsNameList)
                {
                    TextBox textbox = (TextBox)panel1.Controls[columnName];
                    adapter.InsertCommand.Parameters.Add("@" + columnName, textbox.Text);

                }
                conn.Open();
                adapter.InsertCommand.ExecuteNonQuery();
                MessageBox.Show("Adaugare s-a realizat cu succes!");
                conn.Close();
            }
            catch (Exception ex)
            {
                conn.Close();
                MessageBox.Show(ex.Message);
            }
            clear_text_box();
        }

        private void SetSelectedID(int ID)
        {
            this.ID = ID;
        }

        [Obsolete]
        private void dataGridView2_CellContentClick(object sender, DataGridViewCellEventArgs e)
        {

            int id = (int)(dataGridView2.Rows[e.RowIndex].Cells[0].Value);
            SetSelectedID(id);
            List<string> columnsNameList = new List<string>(ConfigurationSettings.AppSettings["columns_name_child"].Split(','));

            foreach (string columnName in columnsNameList)
            {
                var nume = dataGridView2.Rows[e.RowIndex].Cells[columnName].Value;
                TextBox textbox = (TextBox)panel1.Controls[columnName];
                textbox.Clear();
                try
                {
                    nume = Convert.ToString(nume);
                }
                catch(Exception) {}
                textbox.AppendText((string)nume);

            }
        }

        [Obsolete]
        private void button1_Click(object sender, EventArgs e)
        {
            try
            {
                adapter.UpdateCommand = new SqlCommand(ConfigurationSettings.AppSettings["update_child"], conn);

                List<string> columnsNameList = new List<string>(ConfigurationSettings.AppSettings["columns_name_child"].Split(','));
                adapter.UpdateCommand.Parameters.Add("@" + ConfigurationSettings.AppSettings["child_id"], ID);

                foreach (string columnName in columnsNameList)
                {
                    TextBox textbox = (TextBox)panel1.Controls[columnName];
                    adapter.UpdateCommand.Parameters.Add("@" + columnName, textbox.Text);

                }
                conn.Open();
                adapter.UpdateCommand.ExecuteNonQuery();
                MessageBox.Show("Modificarea s-a realizat cu succes!");
                conn.Close();
            }
            catch (Exception ex)
            {
                conn.Close();
                MessageBox.Show(ex.Message);
            }
            clear_text_box();
        }

        [Obsolete]
        private void button2_Click(object sender, EventArgs e)
        {
            try
            {
                string delete = ConfigurationSettings.AppSettings["delete_child"];
                adapter.DeleteCommand = new SqlCommand(delete, conn);
                adapter.DeleteCommand.Parameters.Add("@" + ConfigurationSettings.AppSettings["child_id"], ID);

                conn.Open();
                adapter.DeleteCommand.ExecuteNonQuery ();
                MessageBox.Show("Stergerea s-a realizat cu succes!");
                conn.Close ();
            }
            catch(Exception ex)
            {
                conn.Close();
                MessageBox.Show(ex.Message);
            }
            clear_text_box();
 
        }


        private void clear_text_box()
        {
            List<string> columnsNameList = new List<string>(ConfigurationSettings.AppSettings["columns_name_child"].Split(','));

            foreach (string columnName in columnsNameList)
            {
                TextBox textbox = (TextBox)panel1.Controls[columnName];
                textbox.Clear();
            }
        }

        [Obsolete]
        private void dataGridView1_CellContentClick(object sender, DataGridViewCellEventArgs e)
        {
            int id = (int)dataGridView1.Rows[e.RowIndex].Cells[ConfigurationSettings.AppSettings["parent_id"]].Value;
            SqlCommand sqlCommand = new SqlCommand(ConfigurationSettings.AppSettings["specify_id_select"], conn);
            SqlParameter param = new SqlParameter();
            param.ParameterName = "@" + ConfigurationSettings.AppSettings["parent_id"];
            param.Value = id;
            sqlCommand.Parameters.Add(param);
            adapter.SelectCommand = sqlCommand;
            DataTable dataTable = new DataTable();
            adapter.Fill(dataTable);
            dataGridView2.DataSource = dataTable;


            TextBox textbox = (TextBox)panel1.Controls[ConfigurationSettings.AppSettings["parent_id"]];
            textbox.Clear();
            textbox.AppendText(id.ToString());
        }
    }
}
